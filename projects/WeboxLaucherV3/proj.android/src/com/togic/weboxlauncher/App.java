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

import java.io.File;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author mountains.liu@togic.com @date 2013-2-1
 */
@SuppressLint("SdCardPath")
public class App extends Application {

    private static final String TAG = "MetroLauncher";

    public static final boolean RUN_AS_LAUNCHER = false;

    public static final String MAIN_PROCESS = "com.togic.weboxlauncher";
    public static final String BACKEND_PROCESS = "wl.launcher.backend";

    public static String sProcessName;
    public static String sVersionCode;
    public static String sVersionName;
    public static String sPackageName;

    public static boolean DEBUG = false;

    public static Context sContext;

    public static boolean sBetaVersion = false;

    public static float sDensity = -1f;
    public static float sHeightPixels = -1f;
    public static float sWidthPixels = -1f;

    public void onCreate() {
        LogUtil.v(TAG, "********************* onCreate: " + this);
        super.onCreate();
        createEnv();
    }

    public void onTerminate() {
        LogUtil.v(TAG, "********************* onTerminate: " + this);
        super.onTerminate();
        destroyEnv();
    }

    private void createEnv() {
        File file = new File("/sdcard/Debug");
        if (file != null && file.exists()) {
            DEBUG = true;
        }

        readParams();

        if (!isMainProcess()) {
            return;
        }

        startService(new Intent(this, WLBackendService.class));
    }

    private void destroyEnv() {
        if (!isMainProcess()) {
            return;
        }

        stopService(new Intent(this, WLBackendService.class));
    }

    private void readParams() {
        try {
            final PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            sProcessName = getProcessName(this);
            sPackageName = replaceDot(info.packageName);
            sVersionCode = String.valueOf(info.versionCode);
            sVersionName = info.versionName;

            sBetaVersion = sVersionName.contains("beta");

            sContext = this;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sDensity < 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            sDensity = metrics.density;

            if (metrics.heightPixels > metrics.widthPixels) {
                sHeightPixels = fixHeightPixel(metrics.widthPixels);
                sWidthPixels = metrics.heightPixels;
            } else {
                sHeightPixels = fixHeightPixel(metrics.heightPixels);
                sWidthPixels = metrics.widthPixels;
            }
        }
    }

    public static final boolean isMainProcess() {
        return MAIN_PROCESS.equals(sProcessName);
    }

    public static final boolean isBackendProcess() {
        return BACKEND_PROCESS.equals(sProcessName);
    }

    private static final String getProcessName(Context ctx) {
        final int pid = Process.myPid();
        final ActivityManager actMgr = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo info : actMgr.getRunningAppProcesses()) {
            if (info.pid == pid) {
                return info.processName;
            }
        }

        return null;
    }

    private static final String replaceDot(String str) {
        return str.replace(".", "_");
    }

    // NOTE: display metrics' height is 672px on letv platform, so i have to fix
    // height pixels for compatibility.
    private static final int fixHeightPixel(int height) {
        if (height >= 672 && height <= 720) {
            return 720;
        } else {
            return height;
        }
    }
}
