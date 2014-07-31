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

package com.togic.weboxlauncher.view;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.togic.weboxlauncher.R;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.NetworkUtil;
import com.togic.weboxlauncher.util.NetworkUtil.NetworkState;
import com.togic.weboxlauncher.util.NetworkUtil.OnNetworkStateChangeListener;
import com.togic.weboxlauncher.util.PreferenceUtil;

/**
 * @author ethanhe @date 2013-10-10
 */
public class NetworkStateText extends CustomFontText implements
        OnNetworkStateChangeListener {

    private final static String TAG = "NetworkStateText";

    private final static int MSG_CONNECTED = 1;
    private final static int MSG_CONNECT_FAILED = 2;

    private final static int DELAYED_SHOW = 1 * 1000;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_CONNECTED:
                hide();
                break;
            case MSG_CONNECT_FAILED:
                toWifiSetting(false);
                break;
            }
        }
    };

    private NetworkUtil mWifiUtil;

    public NetworkStateText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NetworkStateText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkStateText(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detach();
    }

    private void attach() {
        if (mWifiUtil == null) {
            mWifiUtil = new NetworkUtil(getContext(), this);
        }

        PreferenceUtil.setShieldDisconnectAlert(getContext(), true);
        PreferenceUtil.setShieldWifiSetting(getContext(), false);
        mWifiUtil.onResume();
    }

    private void detach() {
        mWifiUtil.onPause();
        PreferenceUtil.setShieldDisconnectAlert(getContext(), false);
    }

    private void onConnected() {
        setText(R.string.check_network_connected);
        mHandler.sendEmptyMessageDelayed(MSG_CONNECTED, DELAYED_SHOW);
    }

    private void hide() {
        final ViewParent view = getParent();
        if (view instanceof ViewGroup) {
            ((ViewGroup) view).removeView(this);
        }
    }

    private void toWifiSetting(boolean isNotQuit) {
        hide();
        CommonUtil.showSettingsWifi(getContext(), true, isNotQuit);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        LogUtil.i(TAG, "&&&&&&&&&&&&&&&&& network state: " + text);
    }

    public void onNetworkStateChange(NetworkState networkstate,
            DetailedState wifiState) {
        switch (networkstate) {
        case ETHERNET_CONNECT:
            hide();
            break;
        case WIFI_CONNECT:
            onConnected();
            break;
        case NOT_AVAILABLE_NETWORK:
            toWifiSetting(true);
            break;
        case NOT_AVAILABLE_WIFI:
            setText(R.string.check_network_not_available_network);
            mHandler.sendEmptyMessageDelayed(MSG_CONNECT_FAILED, DELAYED_SHOW);
            break;
        case WIFI_STATE_CHANGE:
            if (wifiState == null || wifiState == DetailedState.DISCONNECTED) {
                return;
            }
            String str = NetworkUtil.getReadableInfo(getContext(), wifiState,
                    true);
            if (str != null && str.length() != 0) {
                setText(str);
            }
            break;
        }
    }

}
