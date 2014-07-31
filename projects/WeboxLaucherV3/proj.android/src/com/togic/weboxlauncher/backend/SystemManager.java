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

package com.togic.weboxlauncher.backend;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.togic.weboxlauncher.ISystemCallback;
import com.togic.weboxlauncher.WLBackendService;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.NetworkUtil;
import com.togic.weboxlauncher.util.UuidUtil;

/**
 * @author mts @date 2013年10月25日
 */
@SuppressLint("HandlerLeak")
public class SystemManager extends BaseManager {

    private static final String TAG = "WLSystemManager";

    public static final int NETWORK_TYPE_NONE = -1;
    public static final int NETWORK_TYPE_WIFI = 0;
    public static final int NETWORK_TYPE_ETHERNET = 1;
    public static final int NETWORK_TYPE_OTHER = 2;

    private static final int MAX_RETRY_UUID = 10;

    public interface LocalMonitor {
        public void onNetworkStateChanged(boolean state);

        public void onScreenStateChanged(boolean state);
    }

    private final ICallbackList mCallbacks = new ICallbackList();
    private final Vector<LocalMonitor> mLocalMonitors = new Vector<LocalMonitor>();

    private WLBackendService mService;
    private BroadcastReceiver mReceiver;
    private Handler mTaskHandler;

    private boolean mNetworkReady = false;
    private boolean mScreenReady = true;
    private boolean mUuidReady = false;

    private int mRetryUuid = 0;

    public SystemManager(WLBackendService s) {
        mService = s;
    }

    public void create() {
        super.create();

        initUuid();
        initReceiver();
        checkNetworkSync();
    }

    public void destroy() {
        super.destroy();
        destroyReceiver();
    }

    public void registerSystemCallback(ISystemCallback cbk) {
        final ICallbackList cbks = mCallbacks;
        if (!cbks.addCallback(cbk)) {
            return;
        }

        if (!checkNetworkSync()) {
            notifyNetworkStateChanged(cbk, mNetworkReady);
        }
    }

    public void unregisterSystemCallback(ISystemCallback cbk) {
        mCallbacks.removeCallback(cbk);
    }

    void registerLocalMonitor(LocalMonitor lm) {
        if (mLocalMonitors.contains(lm)) {
            return;
        }

        mLocalMonitors.add(lm);

        notifyLocalMonitorNetworkStateChanged(lm, mNetworkReady);
        notifyLocalMonitorScreenStateChanged(lm, mScreenReady);
    }

    void unregisterLocalMonitor(LocalMonitor lm) {
        mLocalMonitors.remove(lm);
    }

    boolean hasNetworkCallback() {
        return mCallbacks.size() > 0;
    }

    private void initUuid() {
        mUuidReady = !CommonUtil.isEmptyString(UuidUtil
                .checkLocalUuid(mService));
        LogUtil.v(TAG, "&&&&&&&&&&&& has uuid: " + mUuidReady);
    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    changeScreenStateSync(false);
                } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    changeScreenStateSync(true);
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mService.registerReceiver(mReceiver, filter, null, mTaskHandler);
    }

    private void destroyReceiver() {
        mService.unregisterReceiver(mReceiver);
    }

    private boolean changeScreenStateSync(boolean state) {
        if (mScreenReady != state) {
            mScreenReady = state;
            mRetryUuid = 0;

            notifyLocalMonitorScreenStateChanged(state);

            if (state) {
                scheduleTask(MSG_CHECK_NETWORK, 0);

                if (!mUuidReady && mNetworkReady) {
                    scheduleTask(MSG_CHECK_UUID, 0);
                }
            } else {
                unscheduleTask(MSG_CHECK_NETWORK);
            }

            return true;
        }

        return false;
    }

    private void checkUuidSync() {
        if (mUuidReady) {
            return;
        }

        if (CommonUtil.isEmptyString(UuidUtil.checkUuid(mService))) {
            if (mRetryUuid++ < MAX_RETRY_UUID) {
                scheduleTask(MSG_CHECK_UUID, mService.getShortInterval());
            } else {
                LogUtil.w(TAG, "get uuid error.");
            }
        } else {
            mUuidReady = true;
        }
    }

    boolean checkNetworkSync() {
        final boolean state = NetworkUtil.isNetworkConnected(mService);
        if (mScreenReady
                && (mCallbacks.size() > 0 || mLocalMonitors.size() > 0)) {
            scheduleTask(MSG_CHECK_NETWORK, mService.getShortInterval());
        }

        if (state != mNetworkReady) {
            mNetworkReady = state;

            if (state && !mUuidReady) {
                scheduleTask(MSG_CHECK_UUID, 0);
            }

            notifyLocalMonitorNetworkStateChanged(state);
            notifyNetworkStateChanged(state);

            return true;
        }

        return false;
    }

    private void notifyNetworkStateChanged(boolean state) {
        for (IInterface cbk : mCallbacks.getCallbacks()) {
            notifyNetworkStateChanged((ISystemCallback) cbk, state);
        }
    }

    private void notifyNetworkStateChanged(ISystemCallback cbk, boolean state) {
        try {
            cbk.onNetworkStateChanged(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void notifyLocalMonitorNetworkStateChanged(boolean state) {
        for (LocalMonitor lm : new ArrayList<LocalMonitor>(mLocalMonitors)) {
            notifyLocalMonitorNetworkStateChanged(lm, state);
        }
    }

    private void notifyLocalMonitorNetworkStateChanged(LocalMonitor lm,
            boolean state) {
        lm.onNetworkStateChanged(state);
    }

    private void notifyLocalMonitorScreenStateChanged(boolean state) {
        for (LocalMonitor lm : new ArrayList<LocalMonitor>(mLocalMonitors)) {
            notifyLocalMonitorScreenStateChanged(lm, state);
        }
    }

    private void notifyLocalMonitorScreenStateChanged(LocalMonitor lm,
            boolean state) {
        lm.onScreenStateChanged(state);
    }

    @Override
    protected void createTaskHandler() {
        if (mTaskHandler == null) {
            final HandlerThread t = new HandlerThread("System_Task_Thread");
            t.start();
            mTaskHandler = new InnerHandler(t.getLooper());
        }
    }

    @Override
    protected void destroyTaskHandler() {
        if (mTaskHandler != null) {
            mTaskHandler.removeCallbacksAndMessages(null);

            final Looper l = mTaskHandler.getLooper();
            if (l != Looper.getMainLooper()) {
                l.quit();
            }
        }
    }

    @Override
    protected Handler getTaskHandler() {
        if (mTaskHandler == null) {
            createTaskHandler();
        }
        return mTaskHandler;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected final String getMessage(int msg) {
        switch (msg) {
        case MSG_CHECK_NETWORK:
            return "MSG_CHECK_NETWORK";
        case MSG_CHECK_UUID:
            return "MSG_CHECK_UUID";
        default:
            return "MSG_UNKNOWN";
        }
    }

    private static final int MSG_CHECK_NETWORK = 0x0001;
    private static final int MSG_CHECK_UUID = 0x0002;

    final class InnerHandler extends TaskHandler {

        public InnerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_CHECK_NETWORK:
                checkNetworkSync();
                break;
            case MSG_CHECK_UUID:
                checkUuidSync();
                break;
            }
        }
    }
}
