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

package com.togic.weboxlauncher.notification;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.togic.launcher.model.nf.NFAlbumInfo;
import com.togic.launcher.model.nf.NFHistoryInfo;
import com.togic.launcher.model.nf.NFInfo;
import com.togic.launcher.model.nf.NFTvInfo;
import com.togic.launcher.model.nf.NFVodInfo;
import com.togic.weboxlauncher.R;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author mts @date 2013年12月12日
 */
public class NFInfoManager extends BroadcastReceiver {

    private static final String TAG = "NFInfoManager";

    // NOTE: these actions and extra are defined in
    // MediaTube/src/com/togic/launcher/util/NotificationUtil.java
    private static final String ACTION_TV = "togic.intent.action.NF_INFO_TV";
    private static final String ACTION_VOD = "togic.intent.action.NF_INFO_VOD";
    private static final String ACTION_HISTORY = "togic.intent.action.NF_INFO_HISTORY";
    private static final String ACTION_ALBUM = "togic.intent.action.NF_INFO_ALBUM";

    private static final String EXT_NF_INFO = "togic.intent.extra.NF_INFO";

    private static final String FILE_TV_INFO = "nf-tv-info.obj";
    private static final String FILE_VOD_INFO = "nf-vod-info.obj";
    private static final String FILE_HISTORY_INFO = "nf-history-info.obj";
    private static final String FILE_ALBUM_INFO = "nf-album-info.obj";

    // NOTE: these int codes are correspond with some codes that defined in
    // Classes/PrefixConst.h. DO NOT CHANGE THEM.
    private static final int CODE_TV_INFO = 0;
    private static final int CODE_VOD_INFO = 1;
    private static final int CODE_HISTORY_INFO = 2;
    private static final int CODE_ALBUM_INFO = 3;

    private static final HashMap<String, String> sMap_Action_File = new HashMap<String, String>();
    private static final HashMap<String, Integer> sMap_Action_Code = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> sMap_File_Code = new HashMap<String, Integer>();

    static {
        sMap_Action_File.put(ACTION_TV, FILE_TV_INFO);
        sMap_Action_File.put(ACTION_VOD, FILE_VOD_INFO);
        sMap_Action_File.put(ACTION_HISTORY, FILE_HISTORY_INFO);
        sMap_Action_File.put(ACTION_ALBUM, FILE_ALBUM_INFO);
    }

    static {
        sMap_Action_Code.put(ACTION_TV, CODE_TV_INFO);
        sMap_Action_Code.put(ACTION_VOD, CODE_VOD_INFO);
        sMap_Action_Code.put(ACTION_HISTORY, CODE_HISTORY_INFO);
        sMap_Action_Code.put(ACTION_ALBUM, CODE_ALBUM_INFO);
    }

    static {
        sMap_File_Code.put(FILE_TV_INFO, CODE_TV_INFO);
        sMap_File_Code.put(FILE_VOD_INFO, CODE_VOD_INFO);
        sMap_File_Code.put(FILE_HISTORY_INFO, CODE_HISTORY_INFO);
        sMap_File_Code.put(FILE_ALBUM_INFO, CODE_ALBUM_INFO);
    }

    public interface NFInfoMonitor {
        public void onReceiveNFInfo(int code, ArrayList<NFInfo> infos);
    }

    private static final int MAX_FORCE_UPDATE = 0;
    private static final int FORCE_UPDATE_INTERVAL = 1000;

    private static NFInfoManager sInstance;

    private final HashMap<String, ArrayList<NFInfo>> mTempInfos = new HashMap<String, ArrayList<NFInfo>>();

    private final ArrayList<NFInfoMonitor> mMonitors = new ArrayList<NFInfoMonitor>();

    private final Context mContext;
    private final Handler mHandler;

    private boolean mHasRegister;

    public static final void addNFInfoMonitor(Context ctx, NFInfoMonitor monitor) {
        if (monitor == null) {
            return;
        }

        if (sInstance == null) {
            sInstance = new NFInfoManager(ctx.getApplicationContext());
        }
        sInstance.addMonitor(monitor);
    }

    public static final void removeNFInfoMonitor(NFInfoMonitor monitor) {
        if (monitor != null && sInstance != null) {
            sInstance.removeMonitor(monitor);
        }
    }

    private NFInfoManager(Context ctx) {
        mContext = ctx;
        mHandler = new Handler();

        initTempInfos(ctx);
    }

    private void initTempInfos(Context ctx) {
        ArrayList<NFInfo> info = null;
        for (String action : sMap_Action_File.keySet()) {
            info = restoreInfoFromFile(ctx, getFileNameByAction(action));
            if (info != null) {
                mTempInfos.put(action, info);
            }
        }
    }

    private void registerReceiver() {
        if (mHasRegister) {
            return;
        }

        final Context ctx = mContext;
        final IntentFilter filter = new IntentFilter();
        for (String action : sMap_Action_File.keySet()) {
            filter.addAction(action);
        }

        ctx.registerReceiver(this, filter);
        mHasRegister = true;
    }

    private void unregisterReceiver() {
        if (!mHasRegister) {
            return;
        }

        mContext.unregisterReceiver(this);
        mHasRegister = false;
    }

    private void addMonitor(NFInfoMonitor monitor) {
        if (mMonitors.size() == 0) {
            registerReceiver();
        }

        if (!mMonitors.contains(monitor)) {
            mMonitors.add(monitor);

            forceNotify(monitor, 0, MAX_FORCE_UPDATE);
        }
    }

    private void removeMonitor(NFInfoMonitor monitor) {
        mMonitors.remove(monitor);
        if (mMonitors.size() == 0) {
            unregisterReceiver();
        }
    }

    private void forceNotify(final NFInfoMonitor monitor, int delay,
            final int count) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final HashMap<String, ArrayList<NFInfo>> temp = mTempInfos;
                for (String action : temp.keySet()) {
                    monitor.onReceiveNFInfo(getIntCodeByAction(action),
                            temp.get(action));
                }

                if (count > 0) {
                    forceNotify(monitor, FORCE_UPDATE_INTERVAL, count - 1);
                }
            }
        }, delay);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtil.v(TAG, "received notification: " + action);

        ArrayList<NFInfo> infos = intent
                .getParcelableArrayListExtra(EXT_NF_INFO);
        if (infos == null || infos.size() == 0) {
            infos = getDefaultInfo(context, action);
            if (infos == null) {
                LogUtil.w(TAG, "unknown notification: " + action);
                return;
            }
        }

        if (infos.equals(mTempInfos.get(action))) {
            LogUtil.v(TAG, "same notification, do nothing");
            return;
        }

        final String file = getFileNameByAction(action);
        if (file != null) {
            saveInfoToFile(context, file, infos);
        }
        mTempInfos.put(action, infos);

        for (int i = 0; i < infos.size(); i++) {
            LogUtil.v(TAG, "&&&&&&&&& receive info: " + infos.get(i));
        }

        final Integer intCode = getIntCodeByAction(action);
        if (intCode == null) {
            return;
        }

        for (NFInfoMonitor monitor : mMonitors) {
            monitor.onReceiveNFInfo(intCode.intValue(), infos);
        }
    }

    private static final void saveInfoToFile(Context ctx, String file,
            Object obj) {
        if (CommonUtil.isEmptyString(file)) {
            return;
        }

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(ctx.openFileOutput(file,
                    Context.MODE_PRIVATE));
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            ctx.deleteFile(file);
        } finally {
            CommonUtil.closeIO(oos);
        }
    }

    @SuppressWarnings("unchecked")
    private static final ArrayList<NFInfo> restoreInfoFromFile(Context ctx,
            String file) {
        if (CommonUtil.isEmptyString(file)) {
            return null;
        }

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(ctx.openFileInput(file));
            return (ArrayList<NFInfo>) ois.readObject();
        } catch (FileNotFoundException e) {
            LogUtil.v(TAG, "can't find file: " + file);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.deleteFile(file);
        } finally {
            CommonUtil.closeIO(ois);
        }

        return null;
    }

    private static final ArrayList<NFInfo> getDefaultInfo(Context ctx,
            String action) {
        NFInfo info = null;
        if (ACTION_TV.equals(action)) {
            info = new NFTvInfo(
                    ctx.getString(R.string.notify_info_tv_title_default),
                    ctx.getString(R.string.notify_info_tv_description_default));
        } else if (ACTION_VOD.equals(action)) {
            info = new NFVodInfo(
                    ctx.getString(R.string.notify_info_vod_title_default),
                    ctx.getString(R.string.notify_info_vod_description_default));
        } else if (ACTION_HISTORY.equals(action)) {
            info = new NFHistoryInfo(
                    ctx.getString(R.string.notify_info_history_title_default),
                    ctx.getString(R.string.notify_info_history_description_default));
        } else if (ACTION_ALBUM.equals(action)) {
            info = new NFAlbumInfo(
                    ctx.getString(R.string.notify_info_history_title_default),
                    ctx.getString(R.string.notify_info_history_description_default));
        } else {
            return null;
        }

        final ArrayList<NFInfo> infos = new ArrayList<NFInfo>(1);
        infos.add(info);
        return infos;
    }

    private static final String getFileNameByAction(String action) {
        return sMap_Action_File.get(action);
    }

    private static final int getIntCodeByAction(String action) {
        return sMap_Action_Code.get(action);
    }
}
