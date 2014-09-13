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

package com.togic.weboxlauncher.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author mountains.liu@togic.com @date 2012-11-26
 */
@SuppressLint("HandlerLeak")
public class AppsManager {

    private static final String TAG = "AppsManager";

    public static final HandlerThread sTaskRunner = new HandlerThread(
            "Apps_Task_Runner");
    static {
        sTaskRunner.start();
    }

    public static final String ALL_APPS = "all_apps";

    private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String act = intent.getAction();
            LogUtil.v(TAG, "intent=" + intent + " action=" + act);

            if (Intent.ACTION_PACKAGE_ADDED.equals(act)) {
                addPackage(new String[] {
                    intent.getData().getSchemeSpecificPart()
                });
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(act)) {
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    removePackage(new String[] {
                        intent.getData().getSchemeSpecificPart()
                    });
                } else {
                    // replace package, ACTION_PACKAGE_ADDED will be send later,
                    // so just need handle ACTION_PACKAGE_ADDED.
                    LogUtil.i(TAG, "replace pkg :"
                            + intent.getData().getSchemeSpecificPart());
                }
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE
                    .equals(act)) {
                addPackage(intent
                        .getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST));
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE
                    .equals(act)) {
                removePackage(intent
                        .getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST));
            }
        }
    };

    private final BroadcastReceiver mLocaleReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String act = intent.getAction();
            if (Intent.ACTION_LOCALE_CHANGED.equals(act)) {
                final Handler h = mTaskHandler;
                h.removeMessages(MSG_GET_ALL_APPS);
                h.sendMessageDelayed(h.obtainMessage(MSG_GET_ALL_APPS, true), 0);
            }
        }
    };

    private Context mContext;
    private Handler mTaskHandler;

    private AppInfos mCachedAppInfos = new AppInfos(32);
    private HashMap<String, Filter<AppInfo>> mAppFilters = new HashMap<String, Filter<AppInfo>>();;
    private HashMap<String, ArrayList<AppWatcher>> mAppWatchers = new HashMap<String, ArrayList<AppWatcher>>();

    private int mVersion = 0;

    private static AppsManager sInstance;

    public static AppsManager getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (AppsManager.class) {
                if (sInstance == null) {
                    sInstance = new AppsManager(ctx.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private AppsManager(Context ctx) {
        mContext = ctx;
        mTaskHandler = new TaskHandler(sTaskRunner.getLooper());

    }

    public void onCreate() {
        mAppFilters.put(ALL_APPS, createDefaultAppFilter());

        registerPackageChangeReceiver(mContext);
        getAllApps(false);
    }

    public void onDestroy() {
        // mCachedAppInfos.clear();
        mAppFilters.clear();
        mAppWatchers.clear();

        mTaskHandler.removeCallbacksAndMessages(null);
        unregisterPackageChangeReceiver(mContext);
    }

    public void setAppFilters(final ArrayList<Filter<AppInfo>> filters) {
        if (checkThreadId()) {
            final HashMap<String, Filter<AppInfo>> temp = mAppFilters;
            for (Filter<AppInfo> f : filters) {
                temp.put(f.getFilterKey(), f);
                LogUtil.v(TAG, f.toString());
            }
        } else {
            mTaskHandler.postDelayed(new Runnable() {
                public void run() {
                    setAppFilters(filters);
                }
            }, 0);
        }
    }

    public void addAppFilter(final Filter<AppInfo> filter) {
        if (checkThreadId()) {
            mAppFilters.put(filter.getFilterKey(), filter);
            LogUtil.v(TAG, filter.toString());
        } else {
            mTaskHandler.postDelayed(new Runnable() {
                public void run() {
                    addAppFilter(filter);
                }
            }, 0);
        }
    }

    public void registerPackageWatcher(final String id, final AppWatcher watcher) {
        if (checkThreadId()) {
            ArrayList<AppWatcher> list = mAppWatchers.get(id);
            if (list == null) {
                list = new ArrayList<AppWatcher>(2);
                mAppWatchers.put(id, list);
            }

            if (!list.contains(watcher)) {
                list.add(watcher);
                watcher.onAttached();

                if (mCachedAppInfos.isEmpty()) {
                    return;
                }

                final ArrayList<AppInfo> temp = new ArrayList<AppInfo>(16);
                getAppsByFilter(mAppFilters.get(id), new ArrayList<AppInfo>(
                        mCachedAppInfos.values()), temp);
                //@xjx
                //watcher.onUpdateAllApps(mVersion, id, temp);
                watcher.onUpdateAppsFromLocal(temp);
            }
        } else {
            if (id == null || watcher == null) {
                return;
            }

            final IdAndWatcher obj = new IdAndWatcher(id, watcher);
            final Handler h = mTaskHandler;
            h.removeMessages(MSG_REGISTER, obj);
            h.removeMessages(MSG_UNREGISTER, obj);
            h.sendMessageDelayed(h.obtainMessage(MSG_REGISTER, obj), DELAY_TASK);
        }
    }

    public void unregisterPackageWatcher(final String id,
            final AppWatcher watcher) {
        if (checkThreadId()) {
            final ArrayList<AppWatcher> list = mAppWatchers.get(id);
            if (list != null && list.contains(watcher)) {
                list.remove(watcher);
                watcher.onDetached();
            }
        } else {
            if (id == null || watcher == null) {
                return;
            }

            final IdAndWatcher obj = new IdAndWatcher(id, watcher);
            final Handler h = mTaskHandler;
            h.removeMessages(MSG_REGISTER, obj);
            h.removeMessages(MSG_UNREGISTER, obj);
            h.sendMessageDelayed(h.obtainMessage(MSG_UNREGISTER, obj), 0);
        }
    }

    private Filter<AppInfo> createDefaultAppFilter() {
        final ArrayList<String> exclude = new ArrayList<String>();
        exclude.add("com.android.browser");
        exclude.add("com.android.providers.downloads.ui");
        exclude.add("com.android.gallery3d");
        exclude.add("com.adobe.flashplayer");
        exclude.add("com.amlogic.miracast");
        exclude.add("com.amlogic.netfilebrowser");
        exclude.add("com.farcore.videoplayer");
        exclude.add("com.farproc.wifi.analyzer");
        exclude.add("com.fb.FileBrower");
        exclude.add("com.gsoft.appinstall");
        exclude.add("com.magicandroidapps.iperf");
        exclude.add("com.qzone.tvalbum");
//        exclude.add("com.togic.livevideo");
        exclude.add("tv.doule.upgrade");
//        exclude.add("com.togic.settings");
//        exclude.add("com.togic.filebrowser");
        exclude.add("com.starcor.hunan");
        exclude.add("com.qzone.familyalbum");
        exclude.add("com.togic.mgtv.dynamic");
        exclude.add("com.togic.taskclean");
        return new AppFilter(ALL_APPS, Filter.FLAG_EXCLUDE, null, exclude);
    }

    private void registerPackageChangeReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addDataScheme("package");
        context.registerReceiver(mPackageReceiver, filter, null, mTaskHandler);

        // context.registerReceiver(mLocaleReceiver, new IntentFilter(
        // Intent.ACTION_LOCALE_CHANGED), null, mTaskHandler);
    }

    private void unregisterPackageChangeReceiver(Context context) {
        context.unregisterReceiver(mPackageReceiver);
        // context.unregisterReceiver(mLocaleReceiver);
    }

    private void getAllApps(boolean force) {
        if (checkThreadId()) {
            final AppInfos cached = mCachedAppInfos;
            if (!force && !cached.isEmpty()) {
                return;
            }

            cached.clear();

            final Context context = mContext;
            final PackageManager pm = context.getPackageManager();
            final List<ResolveInfo> infos = pm.queryIntentActivities(
                    getMainIntent(null), 0);
            for (int i = infos.size() - 1; i >= 0; i--) {
                makeAndCacheAppInfo(context, pm, infos.get(i), cached);
                final ComponentInfo cpnInfo = getValidComponent( infos.get(i));
                if(cpnInfo == null)
                	return;
                
                List<ResolveInfo> minfo = pm.queryIntentActivities(
                        getMainIntent(cpnInfo.packageName), 0);
                Log.v("@allapp ------------------", "name :" +cpnInfo.name + "  --pck name : " +  cpnInfo.packageName + "    --   size" + minfo.size());
            }

            mVersion++;
            notifyUpdateAllApps(new ArrayList<AppInfo>(cached.values()));
        } else {
            final Handler h = mTaskHandler;
            h.removeMessages(MSG_GET_ALL_APPS);
            h.sendMessageDelayed(h.obtainMessage(MSG_GET_ALL_APPS, force), 0);
        }
    }

    private void addPackage(String[] pkgs) {
        if (pkgs == null || pkgs.length == 0) {
            return;
        }

        for (String pkg : pkgs) {
            LogUtil.v(TAG, "******* addPackage: " + pkg);
        }

        final Context context = mContext;
        final PackageManager pm = context.getPackageManager();
        final AppInfos cached = mCachedAppInfos;

        List<ResolveInfo> infos;
        ComponentInfo cpnInfo;
        final ArrayList<AppInfo> apps = new ArrayList<AppInfo>(pkgs.length);
        for (String pkg : pkgs) {
            infos = pm.queryIntentActivities(getMainIntent(pkg), 0);
            for (ResolveInfo info : infos) {
                cpnInfo = getValidComponent(info);
                if (pkg.equals(cpnInfo.packageName)) {
                    apps.add(makeAndCacheAppInfo(context, pm, info, cached));
                }
            }
        }

        if (apps.size() > 0) {
            mVersion++;
            notifyUpdateApps(1,apps, apps);
        }
    }

    private void removePackage(String[] pkgs) {
        if (pkgs == null || pkgs.length == 0) {
            return;
        }

        for (String pkg : pkgs) {
            LogUtil.v(TAG, "******* removePackage: " + pkg);
        }

        final Context context = mContext;
        final PackageManager pm = context.getPackageManager();
        final AppInfos cached = mCachedAppInfos;
        final ArrayList<AppInfo> r = new ArrayList<AppInfo>(pkgs.length);
        AppInfo app;
        for (ComponentName cpn : cached.keySet()) {
            app = cached.get(cpn);
            if (app.cpnName == null) {
                continue;
            }
            for (String pkg : pkgs) {
                if (pkg.equals(app.cpnName.getPackageName())) {
                	app.proFlag =-1;
                    r.add(app);
                    break;
                }
            }
        }

        ComponentName cpn;
        for (int i = r.size() - 1; i >= 0; i--) {
            cpn = r.get(i).cpnName;
            app = cached.remove(cpn);
        }

        final ArrayList<AppInfo> u = new ArrayList<AppInfo>(pkgs.length);
        List<ResolveInfo> infos;
        ComponentInfo cpnInfo;
        for (AppInfo aif : r) {
        	String pkg = aif.cpnName.getPackageName();
            infos = pm.queryIntentActivities(getMainIntent(pkg), 0);
            for (ResolveInfo info : infos) {
                cpnInfo = getValidComponent(info);
                if (pkg.equals(cpnInfo.packageName)) {
                    LogUtil.i(TAG, "package : " + pkg + " still existing !");
                    aif.proFlag = 0;
                    u.add(makeAndCacheAppInfo(context, pm, info, cached));
                }
            }
        }

        mVersion++;
        notifyUpdateApps(-1,r, u);
    }

    private void notifyUpdateAllApps(ArrayList<AppInfo> allApps) {
        final HashMap<String, Filter<AppInfo>> filters = mAppFilters;
        final HashMap<String, ArrayList<AppWatcher>> watchers = mAppWatchers;
        ArrayList<AppInfo> temp = null;
        ArrayList<AppWatcher> ws = null;
        for (String id : watchers.keySet()) {
            ws = watchers.get(id);
            if (ws == null || ws.isEmpty()) {
                LogUtil.i(TAG, "can't find watcher by id: " + id);
                continue;
            }

            temp = new ArrayList<AppInfo>(16);
            getAppsByFilter(filters.get(id), allApps, temp);
            for (AppWatcher w : ws) {
            	//@xjx
                //w.onUpdateAllApps(mVersion, id, temp);
            	w.onUpdateAppsFromLocal(temp);
            }
        }
    }

    private void notifyUpdateApps(int flag, ArrayList<AppInfo> removed,
            ArrayList<AppInfo> updated) {
        final HashMap<String, Filter<AppInfo>> filters = mAppFilters;
        final HashMap<String, ArrayList<AppWatcher>> watchers = mAppWatchers;

        ArrayList<AppWatcher> ws = null;
        ArrayList<AppInfo> removeTemp = null;
        ArrayList<AppInfo> updateTemp = null;
        for (String id : watchers.keySet()) {
            ws = watchers.get(id);
            if (ws == null || ws.isEmpty()) {
                LogUtil.i(TAG, "can't find watcher by id: " + id);
                continue;
            }

            removeTemp = new ArrayList<AppInfo>(4);
            updateTemp = new ArrayList<AppInfo>(4);
            getAppsByFilter(filters.get(id), removed, removeTemp);
            getAppsByFilter(filters.get(id), updated, updateTemp);
            if (!removeTemp.isEmpty() || !updateTemp.isEmpty()) {
                for (AppWatcher w : ws) {
                    w.onUpdateApps(mVersion, id, removeTemp, updateTemp);
                    if(1 == flag)
                    {
                    	w.onUpdateAppsFromLocal(updateTemp);
                    }
                    else {
						w.onUpdateAppsFromLocal(removeTemp);
					}
                }
            }
        }
    }

    private void getAppsByFilter(Filter<AppInfo> filter,
            ArrayList<AppInfo> apps, ArrayList<AppInfo> temp) {
        if (filter == null) {
            temp.addAll(apps);
            return;
        }

        temp.addAll(filter.filter(apps));
    }

    private AppInfo makeAndCacheAppInfo(Context context, PackageManager pm,
            ResolveInfo info, AppInfos cached) {
        final ComponentInfo cpnInfo = getValidComponent(info);
        final ComponentName name = new ComponentName(cpnInfo.packageName,
                cpnInfo.name);
        final AppInfo appInfo = AppInfo
                .createFromResolveInfo(context, pm, info);
        appInfo.cpnName = name;
        cached.put(name, appInfo);

        return appInfo;
    }

    private Intent getMainIntent(String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (pkg != null && pkg.length() != 0) {
            intent.setPackage(pkg);
        }
        return intent;
    }

    private static final ComponentInfo getValidComponent(ResolveInfo r) {
        return r.activityInfo != null ? r.activityInfo : r.serviceInfo;
    }

    private static final boolean checkThreadId() {
        return Thread.currentThread().getId() == sTaskRunner.getId();
    }

    private static final int MSG_GET_ALL_APPS = 0x0001;
    private static final int MSG_REGISTER = 0x0002;
    private static final int MSG_UNREGISTER = 0x0003;

    private static final int DELAY_TASK = 1000;

    final class TaskHandler extends Handler {

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GET_ALL_APPS:
                getAllApps((Boolean) msg.obj);
                break;
            case MSG_REGISTER:
                registerPackageWatcher(((IdAndWatcher) msg.obj).id,
                        ((IdAndWatcher) msg.obj).watcher);
                break;
            case MSG_UNREGISTER:
                unregisterPackageWatcher(((IdAndWatcher) msg.obj).id,
                        ((IdAndWatcher) msg.obj).watcher);
                break;
            default:
                break;
            }
        }
    }

    static final class IdAndWatcher {
        String id;
        AppWatcher watcher;

        IdAndWatcher(String i, AppWatcher w) {
            id = i;
            watcher = w;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof IdAndWatcher) {
                final IdAndWatcher other = (IdAndWatcher) o;
                return id.equals(other.id) && watcher == other.watcher;
            }
            return false;
        }
    }
}
