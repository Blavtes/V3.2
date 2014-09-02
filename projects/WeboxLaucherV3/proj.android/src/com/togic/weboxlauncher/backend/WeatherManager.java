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
import android.util.Log;

import com.google.gson.Gson;
import com.togic.weather.Weather;
import com.togic.weather.WeatherFether;
import com.togic.weather.WeatherFether.IWeatherFether;
import com.togic.weboxlauncher.IMetroCallback;
import com.togic.weboxlauncher.WLBackendService;
import com.togic.weboxlauncher.backend.SystemManager.LocalMonitor;
import com.togic.weboxlauncher.model.MJsonInfo;


/**
 * @author mts @date 2014年3月6日
 */
@SuppressLint("HandlerLeak")
public class WeatherManager extends BaseManager implements LocalMonitor{

	private final SystemManager mSystem;

	private Handler mTaskHandler;
	private WeatherFether mWeatherFether;
	private Weather mWeather;
	private WLBackendService mService;

	private boolean mNetworkReady = false;
	private final ICallbackList mMetroCallbacks = new ICallbackList();
	private final String TAG = "WeatherManager";

	public WeatherManager(WLBackendService s, SystemManager m) {
		mSystem = m;
		mService = s;
	}

	public void create() {
		super.create();
		mSystem.registerLocalMonitor(this);
		mWeatherFether = new WeatherFether(mService);
		mWeatherFether.registerIWeatherFether(mWeatherFetchCallback);
		initReceiver();
		
	}
	
	

	public void destroy() {
		super.destroy();

		mSystem.unregisterLocalMonitor(this);
	}

	public void registerMetroCallback(IMetroCallback cbk) {
		if(cbk == null)
		{
			Log.v("@WeatherDate", "=============registerMetroCallback  null!");
			return;
		}
		
		final ICallbackList cbks = mMetroCallbacks;
		if (!cbks.addCallback(cbk)) {
			return;
		}
		if(mWeather == null)
		{
			scheduleTask(MSG_READ_INFO, 0);
		}else {
			notifyWeather(cbk);
		}
		checkNetworkSync();
	}

	public void unregisterMetroCallback(IMetroCallback cbk) {
		mMetroCallbacks.removeCallback(cbk);
	}

	@Override
	public void onNetworkStateChanged(boolean state) {
		
		if (state == mNetworkReady) {
			return;
		}

		mNetworkReady = state;
		if(mNetworkReady)
		scheduleTask(MSG_READ_INFO, 0);
	}



	private boolean checkNetworkSync() {
		return mSystem.checkNetworkSync();
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


	private static final int MSG_READ_INFO = 0x0001;

	final class InnerHandler extends TaskHandler {

		public InnerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_READ_INFO:
				readWeatherDate();
				break;
			}
		}
	}
	
	private void readWeatherDate()
	{
		if(mWeatherFether!= null)
		{
			Log.v("@WeatherDate", "==============readWeatherDate!");	
			mWeatherFether.fenchWeather();
		}else {
			Log.v("@WeatherDate", "==============mWeatherDate null!");	
		}
	}
	
	private void notifyWeather()
	{
		if(mWeather == null)
			return ;
		for (IInterface cbk : mMetroCallbacks.getCallbacks()) {
			notifyWeather((IMetroCallback) cbk);
		}
		
	}
	private void notifyWeather(IMetroCallback cbk)
	{
		if(mWeather == null)
		{
			 Log.v("@WeatherDate", "=============notifyWeather  null!");
			return;
		}

		try {
			  Log.v("@WeatherDate", "=============notifyWeather " + mWeather.toString());
			cbk.onRefreshWeaDate((new Gson()).toJson(new MJsonInfo(mWeather)));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	IWeatherFether mWeatherFetchCallback = new IWeatherFether() {
		
		@Override
		public void localWeatherCallback(Weather weather) {
			// TODO Auto-generated method stub
			if(weather != null && weather.weather!= null && weather.weather.length()>0)
			{

			  mWeather = weather;
			  notifyWeather();
			  Log.v("@WeatherDate", "=============localWeatherCallback " + mWeather.toString());
			  scheduleTask(MSG_READ_INFO, 2*60*60*1000);
			}
			else
			{
				scheduleTask(MSG_READ_INFO, 15*1000);
			}
		}
		
		@Override
		public void localCityCallback(String city) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public void onScreenStateChanged(boolean state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getMessage(int msg) {
		// TODO Auto-generated method stub
		return null;
	}
	private void initReceiver()
	{
		final Context ctx = mService;
        final IntentFilter filter = new IntentFilter();
        String action = "Intent.action.setting.changeWeather";
        filter.addAction(action);

        ctx.registerReceiver(mReceiver, filter);
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			scheduleTask(MSG_READ_INFO, 0);
		}
	};
}
