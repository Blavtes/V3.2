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

package com.togic.taskclean.service;

import com.togic.taskclean.service.TaskClean;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author yao @date Aug 5, 2014
 */
public class TaskCleanService {

    private static final String TASKCLEAN_SERVICE_ACTION = "com.togic.aidl.action.taskcleanservice";
    private TaskClean mTaskClean;
    private Context mContext;

    public TaskCleanService(Context context) {
        mContext = context;
        bindService();
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTaskClean = null;

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("connected");
            mTaskClean = TaskClean.Stub.asInterface(service);
            if (mTaskClean == null) {
                System.out.println("null");
            } else {
                System.out.println("no null");
            }

        }
    };

    public void clean(boolean isKillSystem) {
        System.out.println("clean");
        
        if (mTaskClean != null) {
            System.out.println("no null");
            try {
                mTaskClean.clean(isKillSystem);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("null");
            bindService();
        }
    }
    
    public void addWhiteList(String packageName){
        if(mTaskClean!=null){
            try {
                mTaskClean.addWhiteList(packageName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            bindService();
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setAction(TASKCLEAN_SERVICE_ACTION);
        mContext.bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        mContext.unbindService(conn);
    }
}
