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

package com.togic.weboxlauncher;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.togic.launcher.model.nf.NFInfo;
import com.togic.launcher.util.image.util.PackageUtils;
import com.togic.weboxlauncher.app.AppInfo;
import com.togic.weboxlauncher.app.AppWatcher;
import com.togic.weboxlauncher.app.AppsManager;
import com.togic.weboxlauncher.http.CibnApi;
import com.togic.weboxlauncher.model.ItemData;
import com.togic.weboxlauncher.model.Page;
import com.togic.weboxlauncher.notification.NFInfoManager;
import com.togic.weboxlauncher.notification.NFInfoManager.NFInfoMonitor;
import com.togic.weboxlauncher.notification.Notification;
import com.togic.weboxlauncher.notification.NotificationManager;
import com.togic.weboxlauncher.notification.NotificationManager.NotificationMonitor;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.MetroParser;
import com.togic.weboxlauncher.util.NetworkStateManager;
import com.togic.weboxlauncher.util.NetworkStateManager.NetworkState;
import com.togic.weboxlauncher.util.NetworkStateManager.NetworkStateMonitor;
import com.togic.weboxlauncher.util.NetworkUtil;
import com.togic.weboxlauncher.util.PreferenceUtil;
import com.togic.weboxlauncher.view.NetworkStateText;
import com.umeng.analytics.MobclickAgent;

/**
 * @author mts @date 2013���������12���������10���������
 */
public class WeBoxLauncher extends Cocos2dxActivity implements
		NetworkStateMonitor, NFInfoMonitor, NotificationMonitor, AppWatcher {

	private static final String TAG = "WeBoxLauncher";
	private static WeBoxLauncher sInstance = null;
	static {
		System.loadLibrary("cocos2dcpp");
	}

	private static final int NETWORK_CHECK_DELAY = 5 * 1000;

	private NetworkState mTempState = new NetworkState(-1, -1);

	private boolean mHasBindService = false;
	private boolean mHasStartSettings = false;
	private static final String UMENG_PATH = "umeng_use_launcher_time.txt";
	private static final int NETWORK_UPDATE_TIME = 8;
	private static final int UMENG_UPDATE_TIME = NETWORK_UPDATE_TIME * 8;
	private static final int BASE_TIME = 60;
	private static final int BASE_WORK_COUNT = 1;
	private long NetWorkStateCount = BASE_WORK_COUNT;
	private final static String mCibnUmengEvent = "getMacAddress";
	private final String ACTION_REMOTE_LOWPOWER = "action.remote.lowpower";
	private Handler mMainHandler = new Handler();
	private Runnable mCheckNetworkTask = new Runnable() {
		@Override
		public void run() {
			checkNetwork();
			mHasStartSettings = false;
		}
	};

	private IBackendService mService;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IBackendService.Stub.asInterface(service);
			if (mHasBindService) {
				registerCallback();
			}
		}
	};

	private IMetroCallback.Stub mMetroCallback = new IMetroCallback.Stub() {
		@Override
		public void onRefreshPage(final Page page) {
			runOnGLThread(new Runnable() {
				@Override
				public void run() {
					final ItemData[] Infos = new ItemData[page.getItems()
							.size()];
					page.getItems().toArray(Infos);
					Log.v(TAG, " ////// ishow " + Infos[0].isshow);
					nativeImageDataUpdataNotification(Infos, page.background);
				}
			});
		}

		@Override
		public void onError() {
			LogUtil.v(TAG, "########## onError");
		}

		@Override
		public void onCibnCheckBegin() throws RemoteException {
			// TODO Auto-generated method stub
			runOnGLThread(new Runnable() {
				@Override
				public void run() {
					nativeCibnCheckNotification(0,0);
				}
			});
		}

		@Override
		public void onCibnCheckEnd(final boolean result) throws RemoteException {
			// TODO Auto-generated method stub
			runOnGLThread(new Runnable() {
				@Override
				public void run() {
				    nativeCibnCheckNotification(1,(result ? 1:-1));
				}
			});
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sInstance = this;
		create();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.setDebugMode(true);
	}

	public static void onEvent(Context context, String id, long value) {
		HashMap<String, String> m = new HashMap<String, String>();
		m.put("__ct__", String.valueOf(value));
		MobclickAgent.onEvent(context, id, m);
	} 
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroy();
		sInstance = null;
	}

	public static final void setUmeng(Context ctx, long time) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(ctx.openFileOutput(UMENG_PATH,
					Context.MODE_PRIVATE));
			oos.writeObject(time);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			ctx.deleteFile(UMENG_PATH);
		} finally {
			CommonUtil.closeIO(oos);
		}
	}

	public static final long getUmeng(Context ctx) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(ctx.openFileInput(UMENG_PATH));
			return (Long) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			ctx.deleteFile(UMENG_PATH);
		} finally {
			CommonUtil.closeIO(ois);
		}

		return 0;
	}

	protected void updateUmengData() {

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();
		LogUtil.v(TAG, "isScreenOn " + isScreenOn);
		if (isScreenOn == true) {
			++NetWorkStateCount;
			if (NetWorkStateCount % NETWORK_UPDATE_TIME == BASE_WORK_COUNT) {
				long time = 0;
				time = getUmeng(this);
				LogUtil.v(TAG, "/////////// oldtime " + time);
				long lt = 0;
				if (time == 0) {
					lt = UMENG_UPDATE_TIME;
				} else {
					lt = time + UMENG_UPDATE_TIME;
				}

				LogUtil.v(TAG, "///////////update time " + lt
						+ " netWorkStateCount " + NetWorkStateCount);
				setUmeng(this, lt);
				NetWorkStateCount = BASE_WORK_COUNT;
			}
		}
	}

	protected void pushUmengData() {
		long time = getUmeng(this);

		if (time != 0) {
			long lt = time;
			LogUtil.v(TAG, "//////////////push time " + lt);
			onEvent(this, "use_time", lt / BASE_TIME);
		}
		setUmeng(this, 0);
	}

	@Override
	public void init() {
		super.init();
		initNetworkStateText();
	}

	public Cocos2dxGLSurfaceView onCreateView() {
		Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
		// WeBoxLauncher should create stencil buffer
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

		return glSurfaceView;
	}

	private void create() {
		if (PreferenceUtil.isFirstLaunch(this)) {
			CommonUtil.linkToInitSettings(this);
		}

		AppsManager.getInstance(this).onCreate();
		bindService();
		register();
		registerTickBroadcast();
	}

	private void destroy() {
		AppsManager.getInstance(this).onDestroy();
		unbindService();
		unregister();
		unregisterTickBroadcast();
	}

	private void initNetworkStateText() {
		final TextView text = new NetworkStateText(this);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
		// this color is corresponded with SETTINGCOLOR in PrefixConst.h
		text.setTextColor(Color.argb(255, 99, 101, 99));

		final MarginLayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.RIGHT | Gravity.BOTTOM);
		// bottom margin is corresponded with showNoti->setPosition(ccp(156,
		// 60)) in MainLayer.cpp, and right margin is corresponded with the
		// MainLayer's right margin.
		params.setMargins(0, 0, 60, 60 - 24 / 2);

		addContentView(text, params);
	}

	private void checkNetwork() {
		LogUtil.v(TAG, "&&&&&&&&&&& checkNetwork");
		if (NetworkUtil.isNetworkConnected(this)) {
			return;
		}

		if (!PreferenceUtil.isShieldDisconnectAlert(this)
				&& !PreferenceUtil.isShieldWifiSetting(this)) {
			CommonUtil.showSettings(this, CommonUtil.KEY_SETTINGS_DISCONNECT);
		}
	}

	@Override
	public void onNetworkStateChanged(final int state, final int level) {
		final NetworkState temp = mTempState;

		updateUmengData();

		if (temp.state == state && temp.level == level) {
			LogUtil.v(TAG, "same network state, don't start network setting.");
			return;
		}

		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				nativeNotifyNetworkStateChanged(state, level);
			}
		});

		if (temp.state == NetworkStateManager.STATE_DISABLE
				&& state != NetworkStateManager.STATE_DISABLE) {
			LogUtil.v(TAG,
					"######### network state: disconnected ---> connected");
			temp.set(state, level);

			pushUmengData();
			if (CibnApi.getCibnString() != null) {
				onCibnFaile(CibnApi.getCibnString());
			}

		} else if (temp.state != NetworkStateManager.STATE_DISABLE
				&& state == NetworkStateManager.STATE_DISABLE) {
			LogUtil.v(TAG,
					"######### network state: connected ---> disconnected");
			temp.set(state, level);
		} else {
			temp.set(state, level);
			return;
		}

		if (state == NetworkStateManager.STATE_DISABLE) {
			if (!mHasStartSettings) {
				mMainHandler
						.postDelayed(mCheckNetworkTask, NETWORK_CHECK_DELAY);
				mHasStartSettings = true;
			}
		} else {
			mMainHandler.removeCallbacks(mCheckNetworkTask);
			mHasStartSettings = false;
		}
	}

	@Override
	public void onReceiveNFInfo(final int code, final ArrayList<NFInfo> infos) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				final NFInfo[] array = new NFInfo[infos.size()];
				infos.toArray(array);
				nativeSendNFInfo(code, array);
			}
		});
	}

	@Override
	public void onReceiveNotification(final int code, final Notification info) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				nativeSendNotification(code, info);
			}
		});
	}

	@Override
	public void onReceiveJsonString(final int code, final String json) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				nativeSendAirPlayMusic(code, json);
			}
		});
	}

	@Override
	public void onAttached() {
		LogUtil.v(TAG, "&&&&&&&&&&&&&& onAttached");
	}

	@Override
	public void onUpdateAllApps(final int version, final String id,
			final ArrayList<AppInfo> apps) {
		LogUtil.v(TAG, "&&&&&&&&&&&&&& onUpdateAllApps, all apps: " + apps);
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				final AppInfo[] appInfos = new AppInfo[apps.size()];
				apps.toArray(appInfos);
				nativeUpdateAllApps(version, appInfos);
			}
		});
	}

	@Override
	public void onUpdateApps(final int version, final String id,
			final ArrayList<AppInfo> removed, final ArrayList<AppInfo> updated) {
		LogUtil.v(TAG, "&&&&&&&&&&&&&& onUpdateApps, removed: " + removed
				+ "\nupdated: " + updated);
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				final AppInfo[] removedApps = new AppInfo[removed.size()];
				removed.toArray(removedApps);
				final AppInfo[] updatedApps = new AppInfo[updated.size()];
				updated.toArray(updatedApps);

				nativeUpdateApps(version, removedApps, updatedApps);
			}
		});
	}

	@Override
	public void onDetached() {
		LogUtil.v(TAG, "&&&&&&&&&&&&&& onDetached");
	}

	private void bindService() {
		if (!mHasBindService) {
			mHasBindService = true;
			bindService(new Intent(WLBackendService.ACTION), mConnection,
					Context.BIND_AUTO_CREATE);
		}
	}

	private void unbindService() {
		if (mHasBindService) {
			mHasBindService = false;

			unregisterCallback();
			unbindService(mConnection);
		}
	}

	private void register() {
		AppsManager.getInstance(this).registerPackageWatcher(
				AppsManager.ALL_APPS, this);
		NFInfoManager.addNFInfoMonitor(this, this);
		NotificationManager.addMonitor(this, this);
		NetworkStateManager.addNetworkStateMonitor(this, this);
	}

	private void unregister() {
		AppsManager.getInstance(this).unregisterPackageWatcher(
				AppsManager.ALL_APPS, this);
		NFInfoManager.removeNFInfoMonitor(this);
		NotificationManager.removeMonitor(this);
		NetworkStateManager.removeNetworkStateMonitor(this);
	}

	private static void repeatRegister(boolean flag) {
		sInstance.unregister();
		sInstance.register();
	}

	private static void uninstallPackage(String pckname) {
		PackageUtils.uninstall(sInstance, pckname, false);
	}

	private static void onClickEvt(String pckname, String key) {
		// PackageUtils.uninstall(sInstance, pckname, false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(key, pckname);
		MobclickAgent.onEvent(sInstance, "clicked", map);

	}

	public static void onCibnFaile(String macstr) {
		HashMap<String, String> map = new HashMap<String, String>();
		LogUtil.v(TAG, " cibn auth failer : " + macstr);
		map.put("authFailer", macstr);
		if (sInstance != null) {
			MobclickAgent.onEvent(sInstance, mCibnUmengEvent, map);
		} else {
			Log.v(TAG, "cibn auth failer  instance is null");
		}

	}

	private static String getJsonInfosFromLocal()
	{
		return MetroParser.getInfos(sInstance);
	}

	protected void registerCallback() {
		if (mService == null) {
			return;
		}

		try {
			mService.registerMetroCallback(mMetroCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void unregisterCallback() {
		if (mService == null) {
			return;
		}

		try {
			mService.unregisterMetroCallback(mMetroCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver mTickBroadcast = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			LogUtil.v(TAG, "////////////// update sync time");
			if (Intent.ACTION_TIME_TICK.equals(arg1.getAction())) {
				nativeUpdateTime(0);
			} else if (ACTION_REMOTE_LOWPOWER.equals(arg1.getAction())) {
				nativeLowPower(0);
			}
		}
	};

	private void registerTickBroadcast() {
		IntentFilter intflt = new IntentFilter(Intent.ACTION_TIME_TICK);
		intflt.addAction(ACTION_REMOTE_LOWPOWER);
		registerReceiver(mTickBroadcast, intflt);
	}

	private void unregisterTickBroadcast() {
		unregisterReceiver(mTickBroadcast);
	}

	private native static void nativeNotifyNetworkStateChanged(int state,
			int level);

	private native static void nativeSendNFInfo(int code, NFInfo[] array);

	private native static void nativeSendNotification(int code,
			Notification info);

	private native static void nativeSendAirPlayMusic(int code, String gson);

	private native static void nativeImageDataUpdataNotification(
			ItemData[] info, String background);

	private native static void nativeCibnCheckNotification(int code, int result);

	private native static void nativeUpdateAllApps(int version, AppInfo[] apps);

	private native static void nativeUpdateApps(int version, AppInfo[] removed,
			AppInfo[] updated);

	private native static void nativeUpdateTime(int time);
	private native static void nativeLowPower(int low);
}