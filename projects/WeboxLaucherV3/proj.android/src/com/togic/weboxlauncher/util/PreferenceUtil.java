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

package com.togic.weboxlauncher.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.preference.PreferenceManager;

import com.togic.weboxlauncher.model.Page;

/**
 * @author mountains.liu@togic.com @date 2013-5-15
 */
public class PreferenceUtil {

    private static final String KEY_FIRST_LAUNCH = "togic_box_first_launch";
    private static final String KEY_SHOW_DISCONNECT_ALERT = "show_disconnect_alert";
    private static final String KEY_SHIELD_WIFI_SETTING = "shield_wifi_setting";

    public static final int SHOW_DISCONNECT_ALERT = 1;
    public static final int SHIELD_DISCONNECT_ALERT = 0;
    public static final int SHOW_WIFI_SETTING = 1;
    public static final int SHIELD_WIFI_SETTING = 0;

    private static final String KEY_METRO_FILE = "metro_file_name";
    private static final String KEY_METRO = "metro.obj";

    public static final boolean isFirstLaunch(Context ctx) {
        if (android.provider.Settings.System.getInt(ctx.getContentResolver(),
                KEY_FIRST_LAUNCH, 1) != 0) {
            android.provider.Settings.System.putInt(ctx.getContentResolver(),
                    KEY_FIRST_LAUNCH, 0);
            return true;
        }
        return false;
    }

    public static final void setShieldDisconnectAlert(Context ctx, boolean value) {
        android.provider.Settings.System.putInt(ctx.getContentResolver(),
                KEY_SHOW_DISCONNECT_ALERT, value ? SHIELD_DISCONNECT_ALERT
                        : SHOW_DISCONNECT_ALERT);
    }

    public static final boolean isShieldDisconnectAlert(Context ctx) {
        return android.provider.Settings.System.getInt(
                ctx.getContentResolver(), KEY_SHOW_DISCONNECT_ALERT,
                SHIELD_DISCONNECT_ALERT) == SHIELD_DISCONNECT_ALERT;
    }

    public static final void setShieldWifiSetting(Context ctx, boolean value) {
        android.provider.Settings.System.putInt(ctx.getContentResolver(),
                KEY_SHIELD_WIFI_SETTING, value ? SHIELD_WIFI_SETTING
                        : SHOW_WIFI_SETTING);
    }

    public static final boolean isShieldWifiSetting(Context ctx) {
        return android.provider.Settings.System.getInt(
                ctx.getContentResolver(), KEY_SHIELD_WIFI_SETTING,
                SHIELD_WIFI_SETTING) == SHIELD_WIFI_SETTING;
    }

    public static final void setMetroFileName(Context ctx, String name) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .putString(KEY_METRO_FILE, name).commit();
    }

    public static final String getMetroFileName(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                KEY_METRO_FILE, null);
    }

    public static final void setMetro(Context ctx, Page page) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(ctx.openFileOutput(KEY_METRO,
                    Context.MODE_PRIVATE));
            oos.writeObject(page);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            ctx.deleteFile(KEY_METRO);
        } finally {
            CommonUtil.closeIO(oos);
        }
    }

    public static final Page getMetro(Context ctx) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(ctx.openFileInput(KEY_METRO));
            return (Page) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            ctx.deleteFile(KEY_METRO);
        } finally {
            CommonUtil.closeIO(ois);
        }

        return null;
    }
}
