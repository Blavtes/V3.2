/**
 * Copyright (C) 2014 Togic Corporation. All rights reserved.
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

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.togic.launcher.util.image.impl.FileNameRuleImageUrl;
import com.togic.launcher.util.image.impl.ImageSDCardCache;
import com.togic.launcher.util.image.impl.ImageSDCardCache.OnImageSDCallbackListener;
import com.togic.launcher.util.image.impl.SimpleCache;
import com.togic.launcher.util.image.util.CacheSizeUtils;
import com.togic.weboxlauncher.IMetroCallback;
import com.togic.weboxlauncher.WLBackendService;
import com.togic.weboxlauncher.backend.SystemManager.LocalMonitor;
import com.togic.weboxlauncher.model.MJsonInfo;
import com.togic.weboxlauncher.model.MetroDate;
import com.togic.weboxlauncher.model.Page;
import com.togic.weboxlauncher.util.CibnParser;
import com.togic.weboxlauncher.util.CibnParser.CibnCallback;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.MMetroParser;
import com.togic.weboxlauncher.util.MMetroParser.MParserCallback;

/**
 * @author mts @date 2014年3月6日
 */
@SuppressLint("HandlerLeak")
public class MetroManager extends BaseManager implements LocalMonitor,
		CibnCallback {

	private static final String TAG = "MetroManager";

	private static final String CACHE_FOLDER = "metro";

	private static final int MAX_NETWORK_CHANGED = 5;

	private final ICallbackList mMetroCallbacks = new ICallbackList();

	private final WLBackendService mService;
	private final SystemManager mSystem;

	private Handler mTaskHandler;

	private ImageSDCardCache mMetroCache;

	private boolean mNetworkReady = false;
	private boolean mScreenReady = false;

	private boolean mFirstReadMetro = true;
	private boolean mReadingMetro = false;

	private boolean mCheckedCibn = false;
	private boolean mCheckCibnResult = false;

	private int mNetworkChangeCount = 0;
	private int mRetryMetroCount = 0;

	public MetroManager(WLBackendService s, SystemManager m) {
		mService = s;
		mSystem = m;
		mPcall = new mmcallback();
	}

	public void create() {
		super.create();

		mSystem.registerLocalMonitor(this);
		initMetro();
	}

	public void destroy() {
		super.destroy();

		mSystem.unregisterLocalMonitor(this);
	}

	public void registerMetroCallback(IMetroCallback cbk) {
		final ICallbackList cbks = mMetroCallbacks;
		if (!cbks.addCallback(cbk)) {
			return;
		}

		checkNetworkSync();

		final int delay = mService.getMediumInterval();
		if (mJsonInfo != null) {
			notifyMetroDate(cbk);
			if (mNetworkReady) {
				scheduleTask(MSG_READ_METRO, delay);
				scheduleTask(MSG_CIBN_CHECK_INVOL, 0);
			}
		} else if (!isReadingMetro()) {
			scheduleTask(MSG_READ_METRO, 0);
		}
	}

	public void unregisterMetroCallback(IMetroCallback cbk) {
		mMetroCallbacks.removeCallback(cbk);
	}

	boolean hasMetroCallback() {
		return mMetroCallbacks.size() > 0;
	}

	@Override
	public void onNetworkStateChanged(boolean state) {
		LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&& onNetworkStateChanged: " + state);
		if (state == mNetworkReady) {
			return;
		}

		mNetworkReady = state;
		mNetworkChangeCount++;

		if (!mNetworkReady) {
			if (!mFirstReadMetro) {
				unscheduleTask(MSG_READ_METRO);
			}
			unscheduleTask(MSG_CIBN_CHECK_INVOL);
		} else {
			if (!mScreenReady) {
				return;
			}
			scheduleTask(MSG_READ_METRO, mService.getMediumInterval());
			scheduleTask(MSG_CIBN_CHECK_INVOL, 0);
		}
	}

	@Override
	public void onScreenStateChanged(boolean state) {
		LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&& onScreenStateChanged: " + state);
		if (mScreenReady == state) {
			return;
		}

		mScreenReady = state;
		if (!state) {
			if (!mFirstReadMetro) {
				unscheduleTask(MSG_READ_METRO);
			}
		} else {
			mNetworkChangeCount = 0;

			if (mNetworkReady && !mFirstReadMetro) {
				scheduleTask(MSG_READ_METRO, mService.getMediumInterval());
			}
		}
	}

	private final void initMetro() {
		scheduleTask(MSG_READ_METRO, 0);
	}

	@SuppressWarnings("serial")
	private void initMetroCache() {
		final File cacheDir = CacheSizeUtils.getInternalCacheDir(mService,
				CACHE_FOLDER);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		if (CacheSizeUtils.getUsableSpace(cacheDir) < SimpleCache.DEFAULT_MAX_BYTE_SIZE) {
			LogUtil.w(TAG, "cache folder useable space is not enough");
			return;
		}

		final ImageSDCardCache cache = mMetroCache = new ImageSDCardCache(25,
				ImageSDCardCache.DEFAULT_MAX_BYTE_SIZE);
		cache.setHttpReadTimeOut(10000);
		cache.setOpenWaitingQueue(true);
		cache.setValidTime(-1);

		cache.setFileNameRule(new FileNameRuleImageUrl());
		cache.setCacheFolder(cacheDir.getAbsolutePath());
		cache.setOnImageSDCallbackListener(new OnImageSDCallbackListener() {
			@Override
			public void onImageLoaded(String imageUrl, String imagePath,
					View view, boolean isInCache) {
				LogUtil.v(TAG, "### onImageLoaded: url=" + imageUrl + "  path="
						+ imagePath + "  inCache=" + isInCache);
				if (imageUrl == null) {
					return;
				}

				cache.removeButNotDeleteFile(imageUrl);
				if (!isInCache && imagePath != null) {
					cache.put(cache.getFileNameRule().getFileName(imageUrl),
							imagePath);
				}
			}
		});

		final File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File file : files) {
				LogUtil.v(TAG, "cache file: key=" + file.getName() + "  val="
						+ file.getAbsolutePath());
				cache.put(file.getName(), file.getAbsolutePath());
			}
		}
		LogUtil.v(TAG, "sdcard cache: " + cache.getCacheFolder());
	}

	private boolean isReadingMetro() {
		return mReadingMetro || hasTask(MSG_READ_METRO);
	}

	private void readMetroSync() {
		if (!mNetworkReady && !mFirstReadMetro && mCheckedCibn) {
			LogUtil.v(TAG,
					"network is not ready and not first start, don't read metro");
			return;
		}

		if (mMetroCache == null) {
			initMetroCache();
			if (!mFirstReadMetro && mMetroCache == null) {
				LogUtil.w(TAG, "not first start and cache is null, do nothing.");
				return;
			}
		}

		mReadingMetro = true;
		// MetroParser.parse(mService, this, mMetroCache, mFirstReadMetro,
		// mCheckCibnResult);
		MMetroParser.parse(mService, mFirstReadMetro, mPcall, mMetroCache);

		mReadingMetro = false;
	}

	private void checkCibn() {
		if (!mNetworkReady) {
			LogUtil.v(TAG,
					"network is not ready and not first start, don't read metro");
			return;
		}

		CibnParser.parse(mService, this);
	}

	private boolean checkNetworkSync() {
		return mSystem.checkNetworkSync();
	}

	private void notifyCibnCheckBegin() {
		for (IInterface cbk : mMetroCallbacks.getCallbacks()) {
			try {
				((IMetroCallback) cbk).onCibnCheckBegin();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void notifyCibnCheckEnd(boolean result) {
		for (IInterface cbk : mMetroCallbacks.getCallbacks()) {
			try {
				((IMetroCallback) cbk).onCibnCheckEnd(result);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void createTaskHandler() {
		if (mTaskHandler == null) {
			final HandlerThread t = new HandlerThread("Metro_Task_Thread");
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
	protected String getMessage(int msg) {
		switch (msg) {
		case MSG_READ_METRO:
			return "MSG_READ_METRO";
		default:
			return "MSG_UNKNOWN";
		}
	}

	private static final int MSG_READ_METRO = 0x0001;
	private static final int MSG_CIBN_CHECK_BEGIN = 0x0011;
	private static final int MSG_CIBN_CHECK_END = 0x0012;
	private static final int MSG_CIBN_CHECK_INVOL = 0x0013;

	final class InnerHandler extends TaskHandler {

		public InnerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_READ_METRO:
				readMetroSync();
				break;
			case MSG_CIBN_CHECK_BEGIN:
				notifyCibnCheckBegin();
				break;
			case MSG_CIBN_CHECK_END:
				boolean result = (1 == msg.arg1);
				notifyCibnCheckEnd(result);
				break;
			case MSG_CIBN_CHECK_INVOL:
				checkCibn();
				break;
			}
		}
	}

	@Override
	public void onCheckBegin() {
		// TODO Auto-generated method stub
		scheduleTask(MSG_CIBN_CHECK_BEGIN, 0);
	}

	@Override
	public void onCheckEnd(boolean result) {
		// TODO Auto-generated method stub
		// scheduleTask(MSG_CIBN_CHECK_END, 0);
		Handler h = getTaskHandler();
		Message message = new Message();
		message.what = MSG_CIBN_CHECK_END;
		message.arg1 = result ? 1 : -1;
		// h.sendMessage(message);
		h.sendMessageDelayed(message, 2000);

		mCheckedCibn = true;
		mCheckCibnResult = result;

		scheduleTask(MSG_READ_METRO, 0);
	}

	MJsonInfo mJsonInfo;
	String mBackGroundUrl = null;
	mmcallback mPcall;
	int vesion = -1;

	private void notifyMetroDate(IMetroCallback cbk) {
		try {
			cbk.onRefreshMetroBackground((new Gson()).toJson(new MJsonInfo(mBackGroundUrl)));
			cbk.onRefreshMetroDate((new Gson()).toJson(mJsonInfo));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class mmcallback implements MParserCallback {

		@Override
		public void onPreloadSuccess(String cacheDir) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onParseFinished(boolean firstRead, Page page) {
			// TODO Auto-generated method stub

		}

		@Override
		public synchronized void onParseFinishedN(MetroDate md) {
			// TODO Auto-generated method stub

			int delay = mService.getShortInterval();
			if (null != md) {
				if (mFirstReadMetro) {
					mFirstReadMetro = false;
					delay = mService.getMediumInterval();
				} else {
					delay = mService.getLongInterval();
				}

				if (mJsonInfo == null || vesion < md.version) {

					vesion = md.version;

					mJsonInfo = new MJsonInfo(md.toMinfos());
					mBackGroundUrl = md.background;
					for (IInterface cbk : mMetroCallbacks.getCallbacks()) {
						try {
							((IMetroCallback) cbk).onRefreshMetroBackground((new Gson()).toJson(new MJsonInfo(mBackGroundUrl)));
							String str = (new Gson()).toJson(mJsonInfo);
							((IMetroCallback) cbk).onRefreshMetroDate(str);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					Log.v("@metro", "!!!!!!!!!vesion" + vesion
							+ ">= md.version" + md.version + "delay " + delay);
				}
			}
			if (mNetworkReady && (!isReadingMetro())) {
				scheduleTask(MSG_READ_METRO, delay);
			}
		}
	}
}
