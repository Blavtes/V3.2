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

package com.togic.weboxlauncher.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;

import com.google.gson.Gson;
import com.togic.weboxlauncher.model.MJsonInfo;
import com.togic.weboxlauncher.util.LogUtil;
import com.togic.weboxlauncher.util.MountedDevice;

/**
 * @author mts @date 2014���2���20���
 */
public class NotificationManager {

    private static final String TAG = "NotificationManager";

    public static final String ACTION_SYSTEM_UPGRADE = "togic.intent.action.SYSTEM_UPGRADE_NOTIFICATION";
//    public static final String ACTION_CHASE_DRAMA = "togic.intent.action.CHASE_DRAMA_NOTIFICATION";
    public static final String ACTION_MOUNT_UNMOUNT = "togic.intent.action.MOUNT_UNMOUNT_NOTIFICATION";

    // NOTE: these int codes are correspond with some codes that defined in
    // Classes/PrefixConst.h. DO NOT CHANGE THEM.
    private static final int CODE_SYSTEM_UPGRADE = 10;
    private static final int CODE_CHASE_DRAMA    = 11;
    private static final int CODE_MOUNT_UNMOUNT  = 12;
    private static final int CODE_AIRPLAY_INFO   = 13;
    private static final int CODE_AIRPLAY_STATUS = 14;

    private static final Notification sEmptyNotification = new Notification(false, null);
    private static final HashMap<String, Integer> sMap_Action_Code = new HashMap<String, Integer>();
    static {
        sMap_Action_Code.put(ACTION_SYSTEM_UPGRADE, CODE_SYSTEM_UPGRADE);
//        sMap_Action_Code.put(ACTION_CHASE_DRAMA, CODE_CHASE_DRAMA);
        sMap_Action_Code.put(ACTION_MOUNT_UNMOUNT, CODE_MOUNT_UNMOUNT);
    }

    public interface NotificationMonitor {
        public void onReceiveNotification(int code, Notification n);
        public void onReceiveJsonString(int code, String json);
    }

    private static final String EXT_STATE = "state";
    private static final String EXT_MESSAGE = "message";

    private final HashMap<String, Notification> mTempInfos = new HashMap<String, Notification>();
    private final ArrayList<NotificationMonitor> mMonitors = new ArrayList<NotificationMonitor>();
    private final HashMap<String, Boolean> mMountedUsbMap = new HashMap<String, Boolean>();

    private static NotificationManager sInstance;

    private final Context mContext;
    private final Handler mHandler;

    private CustomReceiver mReceiver1;
    private MediaReceiver mReceiver2;
    private MusicBroadCastReceiver mReceiver3;

    private boolean mHasRegister;

    public static final void addMonitor(Context ctx, NotificationMonitor monitor) {
        if (monitor == null) {
            return;
        }

        if (sInstance == null) {
            sInstance = new NotificationManager(ctx.getApplicationContext());
        }
        sInstance._addMonitor(monitor);
    }

    public static final void removeMonitor(NotificationMonitor monitor) {
        if (monitor != null && sInstance != null) {
            sInstance._removeMonitor(monitor);
        }
    }

    private NotificationManager(Context context) {
        mContext = context;
        mHandler = new Handler();

//        checkMountedUsb();

        for (String action : sMap_Action_Code.keySet()) {
            mTempInfos.put(action, sEmptyNotification);
        }
    }

    private void registerReceiver() {
        if (mHasRegister) {
            return;
        }

        mReceiver1 = new CustomReceiver();
        mReceiver2 = new MediaReceiver();
        mReceiver3 = new MusicBroadCastReceiver();
        mReceiver1.register(mContext);
        mReceiver2.register(mContext);
        mReceiver3.register(mContext);
        mHasRegister = true;
    }

    private void unregisterReceiver() {
        if (!mHasRegister) {
            return;
        }

        mReceiver1.unregister(mContext);
        mReceiver2.unregister(mContext);
        mReceiver3.unregister(mContext);
        mReceiver1 = null;
        mReceiver2 = null;
        mReceiver3 = null;
        mHasRegister = false;
    }

    private void _addMonitor(NotificationMonitor monitor) {
        if (mMonitors.size() == 0) {
            registerReceiver();
        }

        if (!mMonitors.contains(monitor)) {
            mMonitors.add(monitor);

            forceNotify(monitor, 0);
        }
    }

    private void _removeMonitor(NotificationMonitor monitor) {
        mMonitors.remove(monitor);
        if (mMonitors.size() == 0) {
            unregisterReceiver();
        }
    }

    private void checkMountedUsb() {

        ArrayList<MountedDevice> devs = new ArrayList<MountedDevice>();
        MountedDevice.getMountedDevicesByCategory(devs,
                MountedDevice.DEVICE_CATEGORY_UDISK);
        for (MountedDevice item : devs) {
            mMountedUsbMap.put(item.mountPoint, true);
        }
    }

    private void forceNotify(final NotificationMonitor monitor, int delay) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final HashMap<String, Notification> temp = mTempInfos;
                Notification n;
                for (String action : temp.keySet()) {
                    n = temp.get(action);
                    if (!sEmptyNotification.equals(n)) {
                        monitor.onReceiveNotification(
                                getIntCodeByAction(action), temp.get(action));
                    }
                }

                Iterator<String> iter = mMountedUsbMap.keySet().iterator();
                while (iter.hasNext()) {
                    Object mkey = iter.next();
                    Object mval = mMountedUsbMap.get(mkey);
                    LogUtil.v(TAG, "mounted : path=" + mkey + " state=" + mval);

                    monitor.onReceiveNotification(
                            getIntCodeByAction(ACTION_MOUNT_UNMOUNT),
                            new Notification((Boolean) mval, mkey.toString()));
                }
            }
        }, delay);
    }

    private static final int getIntCodeByAction(String action) {
        return sMap_Action_Code.get(action);
    }

    void receive(String action, Notification info) {
        LogUtil.v(TAG, "receive notification: act=" + action + " info=" + info);
        final Integer intCode = getIntCodeByAction(action);
        if (intCode == null) {
            return;
        }

        final HashMap<String, Notification> temps = mTempInfos;
        temps.put(action, info);

        for (NotificationMonitor monitor : mMonitors) {
            monitor.onReceiveNotification(intCode, info);
        }
    }

    final class CustomReceiver extends BroadcastReceiver {

        void register(Context context) {
            final IntentFilter filter = new IntentFilter();
            for (String action : sMap_Action_Code.keySet()) {
                filter.addAction(action);
            }

            context.registerReceiver(this, filter);
        }

        void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            receive(intent.getAction(),
                    new Notification(intent.getBooleanExtra(EXT_STATE, false),
                            intent.getStringExtra(EXT_MESSAGE)));
        }
    }

    final class MediaReceiver extends BroadcastReceiver {

        void register(Context context) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addDataScheme("file");

            context.registerReceiver(this, filter);
        }

        void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            boolean state = false;
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                state = true;
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                state = false;
            } else {
                return;
            }

            final Uri uri = intent.getData();
            if (uri == null) {
                return;
            }

            final String path = uri.getPath();

            receiveMountPath(path, state);
        }
    }

    private void receiveMountPath(String path, Boolean state) {

        LogUtil.v(TAG, "receiveMountPath : path=" + path + " state=" + state);
        if (state) {
            if (mMountedUsbMap.containsKey(path)) {
                return;
            } else {
                mMountedUsbMap.put(path, state);
            }
        } else {
            if (mMountedUsbMap.containsKey(path)) {
                mMountedUsbMap.remove(path);
            } else {
                return;
            }
        }

        receive(ACTION_MOUNT_UNMOUNT, new Notification(state, path));
    }

    final class MusicBroadCastReceiver extends BroadcastReceiver
    {
        void register(Context context) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction("Intent.action.musicairplay");

            context.registerReceiver(this, filter);
        }

        void unregister(Context context) {
            context.unregisterReceiver(this);
        }
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String string = intent.getStringExtra("Intent.action.musicairplay");

            if (string != null) {
                  Log.d("@airplay","airplay --- " +  string);
                  AirplayEvent ae = (new Gson()).fromJson(string, AirplayEvent.class);
                  for (NotificationMonitor monitor : mMonitors) {
                      monitor.onReceiveJsonString(CODE_AIRPLAY_INFO, (new Gson()).toJson(new MJsonInfo(ae)));
                  }
                  return;
			}

            boolean status = intent.getBooleanExtra("music_status", false);
            Log.d(TAG, "MusicBroadCastReceiver " + status);
            for (NotificationMonitor monitor : mMonitors) {
                 monitor.onReceiveJsonString(CODE_AIRPLAY_STATUS,  (new Gson()).toJson(new MJsonInfo(status)));
             }
        }
    }
    
    private class AirplayEvent {
        private int eventType = -1;
        private int intValue = -1;
        private long longValue = -1;
        private float floatValue = -1.0f;
        private String stringValue = null;
        private String stringSession = null;
        private String stringCoverart = null;
        private String stringTitle = null;
        private String stringAlbum = null;
        private String stringArtist = null;
        private boolean booleanValue = false;
    }
}
