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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

import com.togic.remote.http.AbstractHttpApi;
import com.togic.weboxlauncher.App;
import com.togic.weboxlauncher.http.MetroApi;

/**
 * @author mts @date 2013���������12���������13���������
 */
public class CommonUtil {

    private static final String TAG = "CommonUtil";

    private static final String ACT_SETTINGS_MAIN = "togic.intent.action.SETTING_MAIN";
    private static final String ACT_SETTINGS_INIT = "togic.intent.action.SETTING_INIT";
    private static final String ACT_SETTINGS_WIFI = "togic.intent.action.WIFI";

    private static final String EXTRA_IS_GUIDE = "intent.extra.is_guide";
    private static final String EXTRA_IS_BOOT_IN = "intent.extra.is_boot_in";

    public static final String KEY_SETTINGS_DISCONNECT = "22";
    private static Context mContext = null;
//    private static CommonUtil sInstance;
    private static final String[] DATE_PATTERNS = {
        "EEE, dd MMM yyyy HH:mm:ss Z", // RFC 822, updated by RFC 1123
        "EEEE, dd-MMM-yy HH:mm:ss Z", // RFC 850, obsoleted by RFC 1036
        "EEE MMM d HH:mm:ss yyyy" // ANSI C's asctime() format
    };
    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private static final Calendar GMT_CALENDAR = Calendar.getInstance(GMT_ZONE);

    public static final void linkToInitSettings(Context ctx) {
        final Intent intent = new Intent(ACT_SETTINGS_INIT);
        startActivity(ctx, intent);
        mContext = ctx;
    }

    public static final void showSettingsWifi(Context ctx, boolean autoQuit,
            boolean isNotQuit) {
        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&& showSettingsWifi");
        mContext = ctx;
        if (PreferenceUtil.isShieldWifiSetting(ctx)) {
            LogUtil.v(TAG, "&&&&&&&&&&&&&&&&& can show Wifi settings");
            // true: Wifi settings currently being displayed.
            return;
        }

        final Intent intent = new Intent(ACT_SETTINGS_WIFI);
        intent.putExtra(EXTRA_IS_GUIDE, autoQuit);
        intent.putExtra(EXTRA_IS_BOOT_IN, isNotQuit);
        startActivity(ctx, intent);
    }

    public static final void showSettings(Context ctx, String key) {
        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&& showSettings: key=" + key);
        final Intent intent = new Intent(ACT_SETTINGS_MAIN);
        intent.putExtra("intent.extra.CATEGORY_ID", Integer.parseInt(key));
        startActivity(ctx, intent);
    }

    public static final void startActivity(Context ctx, Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String getStringFromFile(String file) {
        try {
            return getStringFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

	public static final String getStringFromCach(Context ctx, String file) {
		FileInputStream inputStream;
		try {
			inputStream = ctx.openFileInput(file);
			return getStringFromInputStream(inputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    public static final String getStringFromFile(File file) {
        try {
            return getStringFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static final String getStringFromAsset(Context ctx, String fileName) {
        try {
            return getStringFromInputStream(ctx.getAssets().open(fileName,
                    AssetManager.ACCESS_STREAMING));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String getStringFromNetwork(String url) {
        HttpClient httpClient = AbstractHttpApi.createHttpClient();
        InputStream is = null;
        try {
            HttpGet httpGet = createHttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                is = AbstractHttpApi.getUngzippedContent(entity);
                return getStringFromInputStream(is);
            } else {
                response.getEntity().consumeContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtil.closeIO(is);
        }

        return "";
    }

    public static final String getStringFromInputStream(InputStream in) {
        if (in == null) {
            return "";
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            final StringBuilder sb = new StringBuilder(in.available());

            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(in);
            closeIO(br);
        }

        return "";
    }

    private static final HttpGet createHttpGet(String url,
            NameValuePair... nameValuePairs) {
        HttpGet httpGet = null;
        if (url != null) {
            String query = URLEncodedUtils.format(
                    stripNullsAndAddExtras(nameValuePairs), HTTP.UTF_8);
            httpGet = new HttpGet(url + (url.contains("?") ? "&" : "?") + query);
            LogUtil.i(TAG, "createHttpGet: " + httpGet.getURI());
        }

        return httpGet;
    }

    private static List<NameValuePair> stripNullsAndAddExtras(
            NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (nameValuePairs != null) {
            for (int i = 0; i < nameValuePairs.length; i++) {
                NameValuePair param = nameValuePairs[i];
                if (param.getValue() != null) {
                    params.add(param);
                }
            }
        }
        return params;
    }

    public static final boolean isEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static final boolean isEqualsString(String s1, String s2) {
        if (s1 == s2) {
            return true;
        } else if (isEmptyString(s1)) {
            return isEmptyString(s2);
        } else {
            return s1.equals(s2);
        }
    }

    public static final boolean isUrlString(String str) {
        return str != null && str.startsWith("http");
    }

    public static final boolean isColorString(String str) {
        return str != null && (str.startsWith("#") || str.startsWith("color"));
    }

    public static final boolean isLocalPath(String str) {
        return str != null && str.startsWith("file");
    }

    public static final boolean isAssetFile(String str) {
        return str != null && str.startsWith("asset");
    }

    public static final String fullUrl(String url) {
        if (isEmptyString(url)) {
            return "";
        } else if (url.startsWith("www") || url.startsWith("192")
                || url.startsWith("http") || isColorString(url)
                || isLocalPath(url) || isAssetFile(url)) {
            return url;
        } else {
            return MetroApi.getInstance(mContext).fullUrl(url);
        }
    }

    public static final boolean writeCache(Context ctx, String fileName,
            String str, String lastModified) {
        FileOutputStream out = null;
        try {
            out = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            out.write(str.getBytes());
            out.flush();

            if (lastModified != null) {
                setCacheFileLastModified(ctx, fileName, lastModified);
            }
            return true;
        } catch (Exception e) {
            LogUtil.w(TAG, "can't write cache file: " + fileName);
        } finally {
            closeIO(out);
        }
        return false;
    }

    public static final String getCacheFileLastModified(Context ctx,
            String fileName) {
        final File f = ctx.getFileStreamPath(fileName);
        if (f != null && f.exists()) {
            return formatDate(f.lastModified());
        } else {
            return null;
        }
    }

    public static final boolean setCacheFileLastModified(Context ctx,
            String fileName, String lastModified) {
        return setFileLastModified(ctx.getFileStreamPath(fileName),
                lastModified);
    }

    public static final boolean setFileLastModified(File f, String lastModified) {
        final Date time = parseDate(lastModified);
        if (f != null && f.exists()) {
            return f.setLastModified(time.getTime());
        }
        return false;
    }

    public static final void closeIO(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final <T> int getArrayLength(T[] array) {
        return array == null ? 0 : array.length;
    }

    public static final int dp2Px(int dp) {
        return Math.round((dp * App.sDensity));
    }

    public static final int scalePixels(int px) {
        return Math.round(px * App.sHeightPixels / 720f);
    }

    public static final Date parseDate(String time) {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
                df.setTimeZone(GMT_ZONE);
                return df.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Date(0);
    }

    public static final String formatDate(long time) {
        final Calendar c = GMT_CALENDAR;
        c.setTimeInMillis(time);
        return String.format(Locale.US, "%ta, %<td %<tb %<tY %<tT GMT", c);
    }
}