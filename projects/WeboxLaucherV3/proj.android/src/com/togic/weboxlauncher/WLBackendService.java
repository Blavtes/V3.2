/**
 * Copyright (C) 2012 Togic Corporation. All rights reserved.
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

import com.togic.weboxlauncher.backend.MetroManager;
import com.togic.weboxlauncher.backend.SystemManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author mountains.liu@togic.com @date 2013-2-28
 */
public class WLBackendService extends Service {

    private static final String TAG = "WLBackendService";

    public static final String ACTION = "togic.intent.action.WEBOX_LAUNCHER.BACKENDSERVICE";

    private final IBackendService.Stub mStub = new IBackendService.Stub() {
        @Override
        public void registerSystemCallback(ISystemCallback cbk) {
            mSystemMgr.registerSystemCallback(cbk);
           
        }

        @Override
        public void unregisterSystemCallback(ISystemCallback cbk) {
            mSystemMgr.unregisterSystemCallback(cbk);
        }

        @Override
        public void registerMetroCallback(IMetroCallback cbk) {
            mMetroMgr.registerMetroCallback(cbk);
        }

        @Override
        public void unregisterMetroCallback(IMetroCallback cbk) {
            mMetroMgr.unregisterMetroCallback(cbk);
        }
    };

    private SystemManager mSystemMgr;
    private MetroManager mMetroMgr;

    @Override
    public void onCreate() {
        super.onCreate();
        createEnv();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            return mStub;
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destryEnv();
    }

    private void createEnv() {
        mSystemMgr = new SystemManager(this);
        mMetroMgr = new MetroManager(this, mSystemMgr);
        mSystemMgr.create();
        mMetroMgr.create();
    }

    private void destryEnv() {
        mSystemMgr.destroy();
        mMetroMgr.destroy();
    }

    private static final int INTERVAL_TASK_LONG = 90 * 60 * 1000;

    private static final int INTERVAL_TASK_MEDIUM = 15 * 1000;

    private static final int INTERVAL_TASK_SHORT = 3 * 1000;

    public final int getLongInterval() {
        return INTERVAL_TASK_LONG;
    }

    public final int getMediumInterval() {
        return INTERVAL_TASK_MEDIUM;
    }

    public final int getShortInterval() {
        return INTERVAL_TASK_SHORT;
    }
}
