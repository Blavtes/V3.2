/*
 * Copyright (C) 2011 Togic Corporation. All rights reserved.
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

package com.togic.launcher.util.image.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * @author libin
 * @date 2012-11-6
 **/
public class CacheSizeUtils {

    private static final String TAG = "CacheSizeUtils";

    public static final boolean DEBUG = false;

    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public static final int IMAGE_SIZE = 512;

    private CacheSizeUtils() {};

    public static int getFileSize(String path) {
        return (int) new File(path).length();
    }

    /**
     * Get the size in bytes of a bitmap.
     * 
     * @param bitmap
     * @return size in bytes
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Check if external storage is built-in or removable.
     * 
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     * 
     * @param context
     *            The context to use
     * @return The external cache dir
     */

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return new File(context.getExternalFilesDir(null), "cache");
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/files/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    public static File getInternalCacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }

    /**
     * Check how much usable space is available at a given path.
     * 
     * @param path
     *            The path to check
     * @return The space available in bytes
     */
    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (path == null) {
            return 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Get the memory class of this device (approx. per-app memory limit)
     * 
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * Check if OS version has built-in external cache dir method.
     * 
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        final File cacheDir = getExternalCacheDir(context, uniqueName);
        if (cacheDir == null) {
            return getInternalCacheDir(context, uniqueName);
        }
        return cacheDir;
    }

    public static File getExternalCacheDir(Context context, String uniqueName) {
        File customerCacheDir = CacheSizeUtils.getExternalCacheDir(context);
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !CacheSizeUtils.isExternalStorageRemovable()
                && customerCacheDir != null && customerCacheDir.canWrite()) {
            return new File(customerCacheDir.getPath() + File.separator
                    + uniqueName);
        }
        return null;
    }

    public static File getInternalCacheDir(Context context, String uniqueName) {
        return new File(CacheSizeUtils.getInternalCacheDir(context).getPath() + File.separator + uniqueName);
    }

}
