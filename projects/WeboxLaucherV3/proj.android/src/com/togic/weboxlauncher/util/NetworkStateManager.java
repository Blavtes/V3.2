/**
 * Copyright (C) 2013 Togic Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.togic.weboxlauncher.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * @author mts @date 2013年12月11日
 */
@SuppressLint("HandlerLeak")
public class NetworkStateManager extends BroadcastReceiver {

    private static final String TAG = "NetworkStateManager";

    private static HandlerThread sTaskThread = new HandlerThread("NETWORK_TASK");
    static {
        sTaskThread.start();
    }

    private static final int WIFI_SIGNAL_LEVEL_TOTAL = 4;
    private static final int UPDATE_INTERVAL = 8000;

    private static final int UPDATE_NET = 0x1001;
    private static final int UPDATE_WIFI = 0x1002;

    public static final int STATE_DISABLE = 0;
    public static final int STATE_ETH = 1;
    public static final int STATE_WIFI = 2;

    final class TaskHandler extends Handler {

        public TaskHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_NET:
                updateNetworkStatus(false);
                break;
            case UPDATE_WIFI:
                updateWifiStatus(false);
                break;
            }
        }
    };

    public static final class NetworkState {
        public int state = STATE_ETH;
        public int level = 0;

        public NetworkState(int s, int l) {
            set(s, l);
        }

        public void set(int s, int l) {
            state = s;
            level = l;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NetworkState) {
                return state == ((NetworkState) o).state
                        && level == ((NetworkState) o).level;
            }
            return false;
        }
    }

    public interface NetworkStateMonitor {
        void onNetworkStateChanged(int state, int level);
    }

    private static final int MAX_FORCE_UPDATE = 1;
    private static final int FORCE_UPDATE_INTERVAL = 1000;

    private static NetworkStateManager mInstance;

    private final Context mContext;
    private final Handler mTaskHandler;

    private boolean mHasRegister;

    private ConnectivityManager mConnManager;
    private WifiManager mWifiManager;

    private List<NetworkStateMonitor> mMonitors = new ArrayList<NetworkStateMonitor>();

    private NetworkStateManager(Context ctx) {
        mContext = ctx;
        mTaskHandler = new TaskHandler(sTaskThread.getLooper());
    }

    public static void addNetworkStateMonitor(Context ctx,
            NetworkStateMonitor monitor) {
        if (monitor == null) {
            return;
        }

        if (mInstance == null) {
            mInstance = new NetworkStateManager(ctx.getApplicationContext());
        }
        mInstance.addMonitor(monitor);
    }

    public static void removeNetworkStateMonitor(NetworkStateMonitor monitor) {
        if (monitor != null && mInstance != null) {
            mInstance.removeMonitor(monitor);
        }
    }

    private void addMonitor(NetworkStateMonitor monitor) {
        if (mMonitors.size() == 0) {
            registerReceiver();
        }

        if (!mMonitors.contains(monitor)) {
            mMonitors.add(monitor);

            forceNotify(monitor, FORCE_UPDATE_INTERVAL, MAX_FORCE_UPDATE);
        }
    }

    private void removeMonitor(NetworkStateMonitor monitor) {
        mMonitors.remove(monitor);
        if (mMonitors.size() == 0) {
            unregisterReceiver();
        }
    }

    private void registerReceiver() {
        if (mHasRegister) {
            return;
        }

        mContext.registerReceiver(this, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
        mHasRegister = true;
    }

    private void unregisterReceiver() {
        if (!mHasRegister) {
            return;
        }

        mContext.unregisterReceiver(this);
        mTaskHandler.removeCallbacksAndMessages(null);
        mHasRegister = false;
    }

    private void forceNotify(final NetworkStateMonitor monitor, int delay,
            final int count) {
        mTaskHandler.removeCallbacksAndMessages(null);
        mTaskHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateNetworkStatus(true);

                if (count > 0) {
                    forceNotify(monitor, FORCE_UPDATE_INTERVAL, count - 1);
                }
            }
        }, delay);
    }

    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION
                .endsWith(intent.getAction())) {
            updateNetworkStatusAsync(FORCE_UPDATE_INTERVAL);
        }
    }

    private void updateNetworkStatusAsync(long delay) {
        mTaskHandler.removeMessages(UPDATE_NET);
        mTaskHandler.removeMessages(UPDATE_WIFI);
        mTaskHandler.sendEmptyMessageDelayed(UPDATE_NET, delay);
    }

    private void updateWifiStatusAsync() {
        mTaskHandler.removeMessages(UPDATE_NET);
        mTaskHandler.removeMessages(UPDATE_WIFI);
        mTaskHandler.sendEmptyMessageDelayed(UPDATE_WIFI, UPDATE_INTERVAL);
    }

    private void updateNetworkStatus(boolean force) {
        if (mConnManager == null) {
            mConnManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        final NetworkInfo info = mConnManager.getActiveNetworkInfo();
        if (info == null) {
            notifyNetworkStateChanged(force, STATE_DISABLE, 0);
            updateNetworkStatusAsync(UPDATE_INTERVAL);
            return;
        } else if (!info.isConnectedOrConnecting()) {
            updateNetworkStatusAsync(UPDATE_INTERVAL);
            return;
        }

        switch (info.getType()) {
        case ConnectivityManager.TYPE_WIFI:
            updateWifiStatus(force);
            return;
        default:
            notifyNetworkStateChanged(force, STATE_ETH, 0);
            updateNetworkStatusAsync(UPDATE_INTERVAL);
            return;
        }
    }

    private void updateWifiStatus(boolean force) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
        }

        final WifiInfo info = mWifiManager.getConnectionInfo();
        if (info == null || invalidSupplicantState(info.getSupplicantState())) {
            updateNetworkStatusAsync(UPDATE_INTERVAL);
            return;
        }

        final int level = WifiManager.calculateSignalLevel(info.getRssi(),
                WIFI_SIGNAL_LEVEL_TOTAL);
        notifyNetworkStateChanged(force, STATE_WIFI, level);

        updateWifiStatusAsync();
    }

    private boolean invalidSupplicantState(SupplicantState state) {
        return state == null && state == SupplicantState.DISCONNECTED
                || state == SupplicantState.UNINITIALIZED
                || state == SupplicantState.INVALID
                || state == SupplicantState.INACTIVE
                || state == SupplicantState.INTERFACE_DISABLED
                || state == SupplicantState.DORMANT
                || state == SupplicantState.SCANNING;
    }

    private void notifyNetworkStateChanged(boolean force, int state, int level) {
        for (NetworkStateMonitor monitor : mMonitors) {
            monitor.onNetworkStateChanged(state, level);
        }
    }
}
