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
import java.util.concurrent.atomic.AtomicBoolean;

import com.togic.weboxlauncher.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Handler;
import android.os.Message;

/**
 * @author ethanhe @date 2013-9-6
 */
public class NetworkUtil {

    private final static String TAG = "NetworkUtil";
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    private static final int DELAYED = 500;

    private final static int SCANNER_NUMBER_MAX = 2;

    public enum NetworkState {
        ETHERNET_CONNECT, WIFI_CONNECT, NOT_AVAILABLE_NETWORK, NOT_AVAILABLE_WIFI, WIFI_STATE_CHANGE
    }

    public interface OnNetworkStateChangeListener {
        void onNetworkStateChange(NetworkState state, DetailedState wifiState);
    }

    private Context mContext;
    private WifiManager mWifiManager;
    private OnNetworkStateChangeListener mListener;
    private IntentFilter mFilter;
    private BroadcastReceiver mReceiver;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private boolean isEtherConnect = false;
    private boolean isCheckAvailable = false;
    private int mScannerNumber = 0;
    private ArrayList<WifiPasswordState> mWifiPasswordList;

    public NetworkUtil(Context ctx, OnNetworkStateChangeListener listener) {
        mContext = ctx;
        mListener = listener;
        mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    }

    private void checkNetworkState() {
        LogUtil.i(TAG, "checkNetworkState");
        if (mListener == null) {
            return;
        }

        if (!isEtherConnect) {
            final int state = mWifiManager.getWifiState();
            LogUtil.i(TAG, "wifi state: " + state);
            if (state != WifiManager.WIFI_STATE_ENABLED
                    && state != WifiManager.WIFI_STATE_ENABLING) {
                LogUtil.i(TAG, "wifi close");
                mListener.onNetworkStateChange(
                        NetworkState.NOT_AVAILABLE_NETWORK, null);
                return;
            }

            final List<WifiConfiguration> configs = mWifiManager
                    .getConfiguredNetworks();
            if (configs != null) {
                if (configs.size() == 0) {
                    LogUtil.e(TAG, "wifi config size is 0");
                    mListener.onNetworkStateChange(
                            NetworkState.NOT_AVAILABLE_NETWORK, null);
                }
            } else {
                LogUtil.e(TAG, "wifi config list is null");
                mHandler.sendEmptyMessageDelayed(0, DELAYED);
            }
        }
    }

    public void onResume() {
        isEtherConnect = isEthernetConnected(mContext);
        LogUtil.i(TAG, "isEtherConnect : " + isEtherConnect);
        initReceiver();
        checkNetworkState();
    }

    private void initReceiver() {
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };
        mContext.registerReceiver(mReceiver, mFilter);
    }

    public void onPause() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG, "action : " + action);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (mListener != null && info != null
                    && info.getState() == State.CONNECTED) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    LogUtil.i(TAG, "wifi connected");
                    mListener.onNetworkStateChange(
                            NetworkState.WIFI_CONNECT, null);
                    isEtherConnect = false;
                } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    mListener.onNetworkStateChange(
                            NetworkState.ETHERNET_CONNECT, null);
                    isEtherConnect = true;
                    LogUtil.i(TAG, "ether connected");
                }
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            if (isEtherConnect) {
                return;
            }
            NetworkInfo info = (NetworkInfo) intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            if (isEtherConnect) {
                return;
            }
            updateAccessPoints();
            SupplicantState state = mWifiManager.getConnectionInfo()
                    .getSupplicantState();
            updateConnectionState(WifiInfo.getDetailedStateOf(state));
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            if (isEtherConnect) {
                return;
            }
            SupplicantState state = (SupplicantState) intent
                    .getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            WifiInfo currentConnectInfo = mWifiManager.getConnectionInfo();

            state = currentConnectInfo.getSupplicantState();
            LogUtil.e(TAG, "ssid : " + currentConnectInfo.getSSID()
                    + "; state : " + state);
            if (!mConnected.get() && state != SupplicantState.SCANNING) {
                updateConnectionState(WifiInfo.getDetailedStateOf(state));
            }
            if (mWifiPasswordList != null) {
                for (WifiPasswordState passwordState : mWifiPasswordList) {
                    passwordState.update(currentConnectInfo.getSSID(), state);
                }
            }
        } else if (EthernetManager.ETH_STATE_CHANGED_ACTION.equals(action)) {
            final int etherState = intent.getIntExtra(
                    EthernetManager.EXTRA_ETH_STATE,
                    EthernetManager.ETH_STATE_UNKNOWN);
            LogUtil.i(TAG, "etherState : " + etherState);
            if (etherState == EthernetStateTracker.EVENT_HW_DISCONNECTED
                    && isEtherConnect
                    && !isEthernetConnected(mContext)) {
                LogUtil.i(TAG, "Ethernet disconnectd");
                isEtherConnect = false;
                checkNetworkState();
            }
        }
    }

    private void updateConnectionState(DetailedState state) {
        LogUtil.i(TAG, "updateConnectionState : " + state);
        if (!mWifiManager.isWifiEnabled()) {
            return;
        }

        if (state != DetailedState.SCANNING) {
            mScannerNumber = 0;
        }
        if (mListener != null && state != null) {
            if (state == DetailedState.IDLE) {
                mListener.onNetworkStateChange(
                        NetworkState.NOT_AVAILABLE_NETWORK, null);
            } else if (state != DetailedState.CONNECTED && !isCheckAvailable) {
                mListener.onNetworkStateChange(NetworkState.WIFI_STATE_CHANGE,
                        state);
            }
        }
    }

    private void updateAccessPoints() {
        final int wifiState = mWifiManager.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            if (mListener == null) {
                return;
            }
            if (!isAvailableNetwork()) {
                LogUtil.i(TAG, "mScannerNumber = " + mScannerNumber);
                if (++mScannerNumber >= SCANNER_NUMBER_MAX) {
                    isCheckAvailable = true;
                    mListener.onNetworkStateChange(
                            NetworkState.NOT_AVAILABLE_WIFI, null);
                }
            }
        }
    }

    public boolean isAvailableNetwork() {
        LogUtil.i(TAG, "isAvailableNetwork");
        final List<WifiConfiguration> configs = mWifiManager
                .getConfiguredNetworks();
        if (configs != null && mWifiPasswordList == null) {
            mWifiPasswordList = new ArrayList<WifiPasswordState>();
            for (WifiConfiguration config : configs) {
                mWifiPasswordList.add(new WifiPasswordState(config.SSID, getSecurity(config)));
            }
        }
        LogUtil.i(TAG, "mWifiPasswordList size : " + mWifiPasswordList.size());
        final List<WifiPasswordState> passwordStates = mWifiPasswordList;
        if (passwordStates != null) {
            if (passwordStates.size() == 0) {
                return false;
            }
            LogUtil.i(TAG, "configs.size() : " + configs.size());
        } else {
            return false;
        }

        boolean found = false;
        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            LogUtil.i(TAG, "results.size() : " + results.size());
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                for (WifiPasswordState state : passwordStates) {
                    if (equalsWifi(result,state) && !state.isPasswordError()) {
                        found = true;
                    }
                }
            }
        }
        LogUtil.i(TAG, "found : " + found);
        return found;
    }

    public static String removeQuote(String string) {
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isConnectedOrConnecting();
        }
        return false;
    }

    public static boolean equalsSSID(String ssid1, String ssid2) {
        ssid1 = removeQuote(ssid1);
        ssid2 = removeQuote(ssid2);
        return ssid1.equals(ssid2);
    }

    public static boolean equalsWifi(ScanResult scan, WifiPasswordState wifi) {
        return equalsSSID(scan.SSID, wifi.ssid)
                && getSecurity(scan) == wifi.security;
    }

    class WifiPasswordState {
        public final static int PASSWORD_STATE_UNVERIFIED = -1;
        public final static int PASSWORD_STATE_ERROR = 0;
        public final static int PASSWORD_STATE_CORRECT = 1;
        public String ssid;
        int isPasswordState = PASSWORD_STATE_UNVERIFIED;
        public int security;
        SupplicantState connectState = SupplicantState.UNINITIALIZED;

        public WifiPasswordState(String ssid, int security) {
            this.ssid = ssid;
            this.security = security;
        }

        public void update(String ssid, SupplicantState state) {
            if (equalsSSID(this.ssid, ssid)) {
                LogUtil.i(TAG, "update ssid " + ssid + "; state " + state);
                if (connectState == SupplicantState.ASSOCIATED) {
                    if (state == SupplicantState.DISCONNECTED) {
                        isPasswordState = PASSWORD_STATE_ERROR;
                    } else if (state == SupplicantState.COMPLETED) {
                        isPasswordState = PASSWORD_STATE_CORRECT;
                    }
                    return;
                }
                connectState = state;
            }
        }

        public boolean isPasswordError() {
            return isPasswordState == PASSWORD_STATE_ERROR;
        }
    }

    public static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public static boolean isEthernetConnected(Context context) {
        EthernetManager mEthManager = (EthernetManager) context
                .getSystemService(Context.ETH_SERVICE);
        return mEthManager.isEthDeviceAdded();
    }

    public static String getReadableInfo(Context context, DetailedState state,
            boolean isChecking) {
        String[] formats;
        if (isChecking) {
            formats = context.getResources().getStringArray(
                    R.array.wifi_status_check);
        } else {
            formats = context.getResources()
                    .getStringArray(R.array.wifi_status);
        }

        int index = state.ordinal();

        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index]);
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeMessages(0);
            checkNetworkState();
        }
    };

}
