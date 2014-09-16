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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import android.R.bool;
import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.togic.launcher.model.nf.NFInfo;
import com.togic.launcher.util.image.util.PackageUtils;
import com.togic.taskclean.service.TaskCleanService;
import com.togic.weather.FileUtil;
import com.togic.weboxlauncher.MCocos2dxGLSurfaceView.KeyCallBack;

import com.togic.weboxlauncher.app.AppInfo;
import com.togic.weboxlauncher.app.AppWatcher;
import com.togic.weboxlauncher.app.AppsManager;
import com.togic.weboxlauncher.http.CibnApi;
import com.togic.weboxlauncher.model.BaseInfo;
import com.togic.weboxlauncher.model.CombData;
import com.togic.weboxlauncher.model.ItemData;
import com.togic.weboxlauncher.model.MJsonInfo;
import com.togic.weboxlauncher.model.Minfo;
import com.togic.weboxlauncher.model.Page;
import com.togic.weboxlauncher.notification.NFInfoManager;
import com.togic.weboxlauncher.notification.NFInfoManager.NFInfoMonitor;
import com.togic.weboxlauncher.notification.Notification;
import com.togic.weboxlauncher.notification.NotificationManager;
import com.togic.weboxlauncher.notification.NotificationManager.NotificationMonitor;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.MMetroParser;
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
	private static TaskCleanService taskCleanService = null;

	private boolean mHasBindService = false;
	private boolean mHasStartSettings = false;
	private static final String UMENG_PATH = "umeng_use_launcher_time.txt";
	private static final String AUTO_START_FILE_PATH = "/data/system/systemBoot";
	private static final String SETTING_SHOWTV_FILE_PTAH = "/data/system/SetingAutoStartTV";
	private static final int NETWORK_UPDATE_TIME = 8;
	private static final int UMENG_UPDATE_TIME = NETWORK_UPDATE_TIME * 8;
	private static final int BASE_TIME = 60;
	private static final int BASE_WORK_COUNT = 1;
	private long NetWorkStateCount = BASE_WORK_COUNT;
	private final static String mCibnUmengEvent = "getMacAddress";
	private final String ACTION_REMOTE_LOWPOWER = "action.remote.lowpower";
	private final String ACTION_TASKCLEAN = "com.togic.taskclean.finish_clean";
	private Handler mMainHandler = new Handler();
	private Runnable mCheckNetworkTask = new Runnable() {
		@Override
		public void run() {
			checkNetwork();
			mHasStartSettings = false;
		}
	};
	
	@Override
	protected void onLoadNativeLibraries()
	{
		return;
	}

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
	static String TAGM = "@xjx----";
	private IMetroCallback.Stub mMetroCallback = new IMetroCallback.Stub() {
		@Override
		public void onRefreshPage(final Page page) {
			
		}

		@Override
		public void onError() {
			LogUtil.v(TAG, "########## onError");
		}

		//------------==================================================================================Send CIBN begin  Info
		@Override
		public void onCibnCheckBegin() throws RemoteException {
			// TODO Auto-generated method stub
			runOnGLThread(new Runnable() {
				@Override
				public void run() {
					// @xjx
					// nativeCibnCheckNotification(0,0);
					int[] resultCode = {0,0};
					MJsonInfo mif = new MJsonInfo(resultCode);
					Gson gson = new Gson();
					String str = gson.toJson(mif);
					Log.v("\r\n@cibn -----@xjx++++++++++++++++receive cibn  json : ", str);
					nativeJsonString(str,"Cibn" );
				}
			});
		}

		//------------==================================================================================Send CIBN update Info
		@Override
		public void onCibnCheckEnd(final boolean result) throws RemoteException {
			// TODO Auto-generated method stub
			runOnGLThread(new Runnable() {
				@Override
				public void run() {
					// @xjx
					// nativeCibnCheckNotification(1,(result ? 1:-1));
					int[] resultCode = {1,result ? 1:-1};
					MJsonInfo mif = new MJsonInfo(resultCode);
					Gson gson = new Gson();
					String str = gson.toJson(mif);
					Log.v("\r\n@cibn -----@xjx++++++++++++++++receive cibn  json : ", str);
					nativeJsonString(str,"Cibn" );
				}
			});
		}

		@Override
		public void onRefreshMetroDate(String date) throws RemoteException {
			// TODO Auto-generated method stub
			Log.v("@IMetroCallback", "=========" + date);
			nativeJsonString(date,"MainApp");
		}

		@Override
		public void onRefreshWeaDate(String date) throws RemoteException {
			// TODO Auto-generated method stub
			nativeJsonString(date,"WeatherState");
		}

		@Override
                public void onRefreshMetroBackground(String date)
                                throws RemoteException {
	                // TODO Auto-generated method stub
			Log.v("@xaxa", "==========================1" + date);
			nativeJsonString(date,"BackgrounImage");
                }
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sInstance = this;
                 taskCleanService = new TaskCleanService(sInstance);
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
		Cocos2dxGLSurfaceView glSurfaceView = new MCocos2dxGLSurfaceView(this);
		// WeBoxLauncher should create stencil buffer
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
		((MCocos2dxGLSurfaceView)glSurfaceView).regcallback(mKeyCallBack);
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

	private static byte[] getIconDataWithPackage(String pack) {
		Log.v("xxxxxxxxxx", "package --- >" + pack);
		byte[] by = null;
		int sIconHeight = 0, sIconWidth = 0;
		try {
			Drawable icon = sInstance.getPackageManager().getApplicationIcon(
					pack);

			if (icon==null)
			{
				return null;
			}

			if (sIconWidth < 1 || sIconHeight < 1) {
				sIconWidth = sIconHeight = (int) sInstance.getResources()
						.getDimension(R.dimen.IconSize);
			}

			final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight,
					Bitmap.Config.ARGB_8888);
			final Canvas canvas = new Canvas();
			canvas.setBitmap(thumb);

			Rect boundsOld = icon.getBounds();
			icon.setBounds(0, 0, sIconWidth, sIconHeight);
			icon.draw(canvas);
			icon.setBounds(boundsOld);
			by = getPixels(thumb);
			Log.v("////////", "icon leng " + by.length);
			// String string = new String(by);
			return by;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("////////", "icon leng" + by.length);
		return null;
	}

	public static byte[] getPixels(final Bitmap bitmap) {
		if (bitmap != null) {
			final byte[] pixels = new byte[bitmap.getWidth()
					* bitmap.getHeight() * 4];
			final ByteBuffer buf = ByteBuffer.wrap(pixels);
			buf.order(ByteOrder.nativeOrder());
			bitmap.copyPixelsToBuffer(buf);
			return pixels;
		}

		return null;
	}
	

	//------------==================================================================================Send NetWork state update Info
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
				// @xjx
				// nativeNotifyNetworkStateChanged(state, level);
				int[] resultCode = {state,level};
				MJsonInfo mif = new MJsonInfo(resultCode);
				Gson gson = new Gson();
				String str = gson.toJson(mif);
				Log.v("\r\n@networkState -----@xjx++++++++++++++++receive networkState  json : ", str);
				nativeJsonString(str,"NetworkState" );
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

	//------------==================================================================================Send MainApp HintText update Info
	@Override
	public void onReceiveNFInfo(final int code, final ArrayList<NFInfo> infos) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				final NFInfo[] array = new NFInfo[infos.size()];
				infos.toArray(array);
				Object[] mainAppHintText = {code,infos.get(0).description,infos.get(0).title};
				MJsonInfo mif = new MJsonInfo(mainAppHintText);
				Gson gson = new Gson();
				String str = gson.toJson(mif);
				Log.v("\r\n@mainAppHintText -----@xjx++++++++++++++++receive mainAppHintText  json : ", str);
//				nativeJsonString(str,"MainAppInfo" );
				// @xjx
				// nativeSendNFInfo(code, array);
			}
		});
	}
	
	//------------==================================================================================Send Notification update Info
	@Override
	public void onReceiveNotification(final int code, final Notification info) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				boolean state = info.state;
				String message = info.message;
				Object[] notificationMessage = {code,state,message};
				MJsonInfo mif = new MJsonInfo(notificationMessage);
				Gson gson = new Gson();
				String str = gson.toJson(mif);
				Log.v("\r\n@NotificationApp -----@xjx++++++++++++++++receive NotificationApp  json : ", str);
				nativeJsonString(str,"NotificationApp" );
				// @xjx
				// nativeSendNotification(code, info);
				
			}
		});
	}
	

	@Override
	public void onReceiveJsonString(final int code, final String json) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				Log.v("@airplay============================", json);
				nativeJsonString(json,"AirPlay");
				// @xjx
				// nativeSendAirPlayMusic(code, json);
				
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
				// @xjx
				 //nativeUpdateAllApps(version, appInfos);
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

				// @xjx
				// nativeUpdateApps(version, removedApps, updatedApps);
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

	private static String getJsonInfosFromLocal() {
		return MMetroParser.initParse(sInstance);
	}
	private static void getBeginClearnMemory(boolean issystem) {
		Log.v(TAG, " getClearnMemory  " + issystem);
		taskCleanService.clean(issystem);
	}
	
	private static String beginAutoStartActivity() {
		Log.v(TAG, " ///// nativeSendAutoStartApp1 ");
		try {
			StringBuilder sBuilder = FileUtil
					.readFile(AUTO_START_FILE_PATH);
			Log.v(TAG, " ///// nativeSendAutoStartApp2 ");
			if(sBuilder == null)
			{
				Log.v(TAG, " ///// nativeSendAutoStartApp1 null");
				return "null";
			}
			String string = sBuilder.toString();
			if(string == null)
			{
				Log.v(TAG, " ///// nativeSendAutoStartApp1 string null");
				return "null";
			}
			return string;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "null";
	}

	private static void sendBroadcastToSettingShowTV() {
		Log.v(TAG, "######## moveXMLFileToDataSys ");
		Intent intent = new Intent();
		intent.setAction("Intent.action.launchershowTV");
		intent.putExtra("Intent.action.launchershowTV", true);
//		sInstance.sendBroadcast(intent);
		sInstance.sendStickyBroadcast(intent);
	}
	
	
	//------------==================================================================================Send memory clean  info
	public void postMemoryLong(final float memory) {
		runOnGLThread(new Runnable() {
			@Override
			public void run() {
				String memstr = "" + memory;
				int deu = memstr.indexOf(".");
				if( deu> 0)
				{
					memstr = memstr.substring(0,deu+2);
				}
				nativeJsonString(memstr,"ClearMemory" );
			}
		});
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
		public void onReceive(Context arg0, final Intent arg1) {
			LogUtil.v(TAG, "////////////// update sync time");
			if (Intent.ACTION_TIME_TICK.equals(arg1.getAction())) {
				// @xjx
				// nativeUpdateTime(0);
			} else if (ACTION_REMOTE_LOWPOWER.equals(arg1.getAction())) {
				// @xjx
				// nativeLowPower(0);
			}else if (ACTION_TASKCLEAN.equals(arg1.getAction())) {
				Log.v(TAG, Float.parseFloat(arg1.getExtra(
						"release memory").toString())
						+ " M  memory ");
				new Timer().schedule(new TimerTask() {
					public void run() {
						postMemoryLong(Math.abs(Float.parseFloat(arg1.getExtra(
								"release memory").toString())));
					}
				}, 2000);
			}
		}
	};

	private void registerTickBroadcast() {
		IntentFilter intflt = new IntentFilter(Intent.ACTION_TIME_TICK);
		intflt.addAction(ACTION_REMOTE_LOWPOWER);
		intflt.addAction(ACTION_TASKCLEAN);
		registerReceiver(mTickBroadcast, intflt);
	}

	private void unregisterTickBroadcast() {
		unregisterReceiver(mTickBroadcast);
	}

	// private native static void nativeNotifyNetworkStateChanged(int state,
	// int level);
	//
	// private native static void nativeSendNFInfo(int code, NFInfo[] array);
	//
	// private native static void nativeSendNotification(int code,
	// Notification info);
	//
	// private native static void nativeSendAirPlayMusic(int code, String gson);
	//
	// private native static void nativeImageDataUpdataNotification(
	// ItemData[] info, String background);
	//
	// private native static void nativeCibnCheckNotification(int code, int
	// result);
	//
	// private native static void nativeUpdateAllApps(int version, AppInfo[]
	// apps);
	//
	// private native static void nativeUpdateApps(int version, AppInfo[]
	// removed,
	// AppInfo[] updated);
	//
	// private native static void nativeUpdateTime(int time);
	// private native static void nativeLowPower(int low);

	private native static boolean nativeJsonString(String str, String dest);

	//------------==================================================================================start the App Activity by info
	private static void mStartActivity(String action, String pckname, String classname) {
		Log.v("@allapp ---", "start activity " +  action);

		Intent intent = new Intent();
		
		if(null != action)
		{
			if(action.equals("togic.intent.action.ONLINE_VIDEO"))
			{
				intent.putExtra("intent.extra.is_entrance_actvitiy", false);
				intent.putExtra("intent.extra.exit_directly", true);
				intent.putExtra("intent.extra.show_splash", false);
			}
			else if (action.equals("togic.intent.action.LIVE_TV"))
			 {
				intent.putExtra("intent.extra.is_entrance_actvitiy", false);
				 intent.putExtra("intent.extra.exit_directly", true);
			 }
			intent.setAction(action);
		}
		if(null != pckname)
		{
			intent.setPackage(pckname);
		}
		if(null !=  classname)
		{
//			intent.setClassName(sInstance, classname);
			intent.setClassName(pckname, classname);
		}
		sInstance.startActivity(intent);
		return;
	}

	

	@Override
	//-++++++++++++++================================================send message to update User Apps
	public void onUpdateAppsFromLocal(ArrayList<AppInfo> updated) {
		// TODO Auto-generated method stub
		AppInfo taginfo0 = null,  taginfo1 = null,  taginfo2 = null;
		for(int i = 0 ; i < updated.size(); i++)
		{
			AppInfo iaif = updated.get(i);
			if(iaif.getPackageName().equals("com.togic.settings"))
			{
				taginfo0 = iaif;
			}
			else if (iaif.getPackageName().equals("com.togic.filebrowser")){

				taginfo1 = iaif;
			}
			else if(iaif.getPackageName().equals("com.togic.appstore")){

				taginfo2 = iaif;
			}
			
		}
		if(null != taginfo0)
		{
			updated.set(0, taginfo0);
			Log.v("mainApp", "taginfo0" + taginfo0.getPackageName());
			Log.v("mainApp", "taginfo0 set " + updated.get(2).getPackageName());
		}
		if(null != taginfo1)
		{
			if(updated.size() >1)
			{
        			updated.set(1, taginfo1);
        			Log.v("mainApp", "taginfo1" + taginfo1.getPackageName());
        			Log.v("mainApp", "taginfo1 set " + updated.get(1).getPackageName());
			}
		}
		if(null != taginfo2)
		{
			if(updated.size() >2)
			{
        			updated.set(2, taginfo2);
        			Log.v("mainApp", "taginf2" + taginfo2.getPackageName());
        			Log.v("mainApp", "taginfo2 set " + updated.get(0).getPackageName());
			}
		}
		
		final AppInfo[] updatedApps = new AppInfo[updated.size()];
		updated.toArray(updatedApps);
		
		BaseInfo[] infos = new BaseInfo[updated.size()];
		
		for (int i = 0; i < updated.size(); i++) {
			infos[i] = new BaseInfo(updatedApps[i]);
		}
		MJsonInfo mif = new MJsonInfo(infos);
		Gson gson = new Gson();
		String str = gson.toJson(mif);
		Log.v("\r\n@allapp -----@xjx----------------------receive user APP  json : \n", str);
		nativeJsonString(str,"UserApp");
	}
	
	private KeyCallBack mKeyCallBack = new KeyCallBack() {
		
		@Override
		public void keyEvnDown(int code) {
			// TODO Auto-generated method stub

			nativeJsonString("code:" + code,"keybox" );
		}
	};

}
