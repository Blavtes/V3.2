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

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings.System;

/**
 * @author mts @date 2013年12月20日
 */
public class UuidUtil {

    private static final String TAG = "UuidUtil";

    private static final String MAC_PREFIX = "/sys/class/net/";

    private static String ONLINE_CUSTOM_CHANNEL_DOMAIN = "http://my.togic.com";

    private static final String URL_API_REGISTER = "/api/register?";

    private static final String UUID = "uuid";

    private static final int[] INDIVIDER_INDEX = {
            8, 12, 16, 20
    };

    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String getUuid(Context ctx) {
        final PowerManager pmgr = (PowerManager) ctx
                .getSystemService(Context.POWER_SERVICE);
        String id = restoreUuid(pmgr.getDeviceId());
        if (isValidUuid(id)) {
            LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&& get device uuid: " + id);
            return id;
        }

        return "";
    }

    // run in backend service
    public static synchronized final String checkUuid(Context ctx) {
        String id = checkLocalUuid(ctx);
        if (!CommonUtil.isEmptyString(id)) {
            return id;
        }

        id = getUuidFromServer(ctx);
        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&& uuid from server: " + id);
        return id;
    }

    // run in backend service
    public static synchronized final String checkLocalUuid(Context ctx) {
        final PowerManager pmgr = (PowerManager) ctx
                .getSystemService(Context.POWER_SERVICE);
        String id = restoreUuid(pmgr.getDeviceId());
        if (isValidUuid(id)) {
            LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&& device uuid: " + id);
            final String temp = System
                    .getString(ctx.getContentResolver(), UUID);
            if (!id.equals(temp)) {
                LogUtil.w(TAG, "invalid uuid in settings db, replace it: "
                        + temp);
                System.putString(ctx.getContentResolver(), UUID, id);
            }

            return id;
        }

        id = System.getString(ctx.getContentResolver(), UUID);
        if (isValidUuid(id)) {
            LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&& setting uuid: " + id);
            if (setDeviceId(pmgr, id)) {
                return id;
            }
        }

        return "";
    }

    private static final boolean setDeviceId(PowerManager pmgr, String id) {
        id = id.replaceAll("-", "");
        if (pmgr.setDeviceId(id)) {
            LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&& set device id: " + id);
            return true;
        } else {
            return false;
        }
    }

    private static String restoreUuid(String uuid) {
        if (CommonUtil.isEmptyString(uuid)) {
            return "";
        } else if (uuid.length() != 32) {
            return "";
        } else {
            final StringBuilder temp = new StringBuilder(uuid.length()
                    + INDIVIDER_INDEX.length);
            temp.append(uuid);
            for (int i = INDIVIDER_INDEX.length - 1; i >= 0; i--) {
                temp.insert(INDIVIDER_INDEX[i], "-");
            }

            return temp.toString();
        }
    }

    private static String getUuidFromServer(Context context) {
        final String id = CommonUtil.getStringFromNetwork(getFullNameUrl(
                URL_API_REGISTER, getDeviceInfo()));
        if (isValidUuid(id)) {
            final PowerManager pmgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            if (setDeviceId(pmgr, id)) {
                System.putString(context.getContentResolver(), UUID, id);
                return id;
            }
        }

        return "";
    }

    private static String getFullNameUrl(String name, String extra) {
        return ONLINE_CUSTOM_CHANNEL_DOMAIN + name + extra;
    }

    private static String getDeviceInfo() {
        final StringBuilder builder = new StringBuilder(512);
        final HashMap<String, String> macAddressMap = getMacAddress();

        String info;
        for (String netIFName : macAddressMap.keySet()) {
            if (netIFName.startsWith("eth")) {
                info = "eth-mac=" + macAddressMap.get(netIFName) + "&";
                builder.append(info);
            } else if (netIFName.startsWith("wlan")) {
                info = "wifi-mac=" + macAddressMap.get(netIFName) + "&";
                builder.append(info);
            }
        }

        info = "device=" + Build.DEVICE + "&";
        builder.append(info);
        info = "model=" + Build.MODEL + "&";
        builder.append(info);
        info = "product=" + Build.PRODUCT;
        builder.append(info);

        info = builder.toString().replace(" ", "_");
        LogUtil.v(TAG, "get device info is: " + info);
        return info;
    }

    private static HashMap<String, String> getMacAddress() {
        final HashMap<String, String> macAddressMap = new HashMap<String, String>();
        try {
            final Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces();

            String netIFName;
            NetworkInterface networkInterface;
            while (enumeration.hasMoreElements()) {
                networkInterface = enumeration.nextElement();
                netIFName = networkInterface.getDisplayName();

                if (netIFName.startsWith("wlan") || netIFName.startsWith("eth")) {
                    String macAddrPath = MAC_PREFIX + netIFName + "/address";
                    String macAddr = CommonUtil.getStringFromFile(new File(
                            macAddrPath));
                    if (!macAddr.equals("")) {
                        macAddressMap.put(netIFName, macAddr);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return macAddressMap;
    }

    private static boolean isValidUuid(String id) {
        if (id == null) {
            return false;
        }

        return id.matches(UUID_REGEX);
    }
}
