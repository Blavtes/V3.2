/*
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

package com.togic.weboxlauncher.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.togic.remote.http.AbstractHttpApi;
import com.togic.remote.http.HttpApiWithBasicAuth;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;

public class MetroApi {

    private static final String TAG = "MetroApi";

    public static final String DOMAIN = "http://qvod.togic.com/";
    public static final String API = "metro";

    private final String mApi;
    private String mBaseUrl;
    private int sWidthPixels;
    private final DefaultHttpClient mHttpClient;
    private final HttpApiWithBasicAuth mHttpApi;
    private final int DEFALUT720P = 720;
    private final int DEFALUT1080P = 1080;

    private static MetroApi sInstance;
    public static final MetroApi getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (MetroApi.class) {
                if (sInstance == null) {
                    sInstance = new MetroApi(DOMAIN, API,ctx);
                }
            }
        }

        return sInstance;
    }

    private MetroApi(String domain, String api ,Context ctx) {
		mApi = api; // just used when reset domain
		mBaseUrl = createBaseUrl(domain);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		LogUtil.v(TAG, "############ base url: " + mBaseUrl + " width Pixe = " + sWidthPixels + " density = "+ metrics.density);
		if (metrics.widthPixels > metrics.heightPixels) {
			if (metrics.widthPixels <= 1280.0f) {
				sWidthPixels = DEFALUT720P;
			} else {
				sWidthPixels  = DEFALUT1080P;
			}
		} else {
			if (metrics.heightPixels <= 720.0f) {
				sWidthPixels = DEFALUT720P;
			} else {
				sWidthPixels = DEFALUT1080P;
			}
		}
		
        mHttpClient = AbstractHttpApi.createHttpClient();
        mHttpApi = new HttpApiWithBasicAuth(mHttpClient, null);
    }

    protected void resetDomain(String domain) {
        mBaseUrl = createBaseUrl(domain);
    }

    private String createBaseUrl(String domain) {
        final StringBuilder sb = new StringBuilder(domain);
        if (!domain.startsWith("http://")) {
            sb.insert(0, "http://");
        }

        if (!domain.endsWith("/")) {
            sb.append("/");
        }

        return sb.append(mApi).toString();
    }

    public String getString(Context ctx, String fileName, boolean cibnResult) {
        final String url = fullUrl(fileName,cibnResult);
        final String lastModified = CommonUtil.getCacheFileLastModified(ctx,
                fileName);

        final HttpGet get = mHttpApi.createHttpGet(url);
        LogUtil.i("@metro", "url : " + url);
        if (!CommonUtil.isEmptyString(lastModified)) {
            get.addHeader("If-Modified-Since", lastModified);
        }

        String str = null;
        try {
            final HttpResponse resp = mHttpApi.executeHttpRequest(get);
            final int statusCode = resp.getStatusLine().getStatusCode();
            LogUtil.v("@metro", "statusCode is " + statusCode);
            switch (statusCode) {
            case 200:
                str = CommonUtil.getStringFromInputStream(AbstractHttpApi
                        .getUngzippedContent(resp.getEntity()));
                break;
            case 304:
                // not changed
                break;
            case 403:
                break;
            default:
                resp.getEntity().consumeContent();
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }
    public final String fullUrl(String url) {
        if (!url.startsWith("/")) {
            return mBaseUrl + "/" + url;
        } else {
            return mBaseUrl + url;
        }
    }
    public final String fullUrl(String url, boolean cibnResult) {
        String str =  "?result=" + cibnResult + "&resolution=" + sWidthPixels;
        if (!url.startsWith("/")) {
            return mBaseUrl + "/" + url + str;
        } else {
            return mBaseUrl + url + str;
        }
    }

    private static final String getHeader(Header[] headers) {
        if (headers == null || headers.length < 1) {
            return "";
        } else {
            return headers[0].getValue();
        }
    }
}
