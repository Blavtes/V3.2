package com.togic.weboxlauncher.http;

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

import java.io.IOException;
import java.io.InputStream;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.xmlpull.v1.XmlPullParser;

import android.R.string;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.togic.remote.http.AbstractHttpApi;
import com.togic.remote.http.HttpApiWithBasicAuth;
import com.togic.weboxlauncher.WeBoxLauncher;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

public class CibnApi {

	private static final String TAG = "cibnapi";

	public static final String DOMAIN = "http://tms.ott.cibntv.net/userCenter/tms/deviceInit.action?mac=";
	public static final String LURL = "http://tms.ott.cibntv.net/userCenter/tms/device!deviceLogin.action?";
	public static final String API = "";

	private final String mApi;
	private String mBaseUrl;
	public static String CIBNFALIR = null;
	private final DefaultHttpClient mHttpClient;
	private final HttpApiWithBasicAuth mHttpApi;
	private static CibnApi sInstance;
	public static final CibnApi getInstance() {
		if (sInstance == null) {
			synchronized (CibnApi.class) {
				if (sInstance == null) {
					sInstance = new CibnApi(DOMAIN, API);
				}
			}
		}

		return sInstance;
	}

	private CibnApi(String domain, String api) {
		mApi = api; // just used when reset domain
		mBaseUrl = createBaseUrl(domain);

		LogUtil.v(TAG, "############ base url: " + mBaseUrl);
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
		return sb.append(mApi).toString();
	}

	public final String fullUrl(String url) {
		return mBaseUrl + url;
	}

	private static final String getHeader(Header[] headers) {
		if (headers == null || headers.length < 1) {
			return "";
		} else {
			return headers[0].getValue();
		}
	}

	public boolean CibnActivate(Context ctx, String fileName) {

		boolean result = false;
		final String url = fullUrl(fileName);
		LogUtil.v(TAG, "******* CibnActivate!!! url: " + url);

		final HttpGet get = mHttpApi.createHttpGet(url);

		String str = null;
		try {
			final HttpResponse resp = mHttpApi.executeHttpRequest(get);
			final int statusCode = resp.getStatusLine().getStatusCode();
			LogUtil.v(TAG, "statusCode is " + statusCode);
			switch (statusCode) {
			case 200:
				str = CommonUtil.getStringFromInputStream(AbstractHttpApi
						.getUngzippedContent(resp.getEntity()));

				LogUtil.v(TAG, "******* CibnActivate!!! str: " + str);

				if (!CommonUtil.isEmptyString(str)) {
					CommonUtil.writeCache(ctx, "cibnactivate.xml", str, null);
					result = true;
				} else {
					// str = CommonUtil.getStringFromInputStream(ctx
					// .openFileInput(fileName));
				}

				// CibnInfo info =
				// getInfo(ctx.openFileInput("cibnactivate.xml"));
				// LogUtil.v(TAG, "******* xml !!!!! " + info.id + "  "+
				// info.result);

				break;
			case 304:
				// not changed
				str = CommonUtil.getStringFromInputStream(ctx
						.openFileInput(fileName));
				break;
			case 403:
				break;
			default:
				resp.getEntity().consumeContent();
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getCibnString() {
		return CIBNFALIR;
	}

	public boolean CibnLaunche(Context ctx, String fileName) {

		boolean result = false;
		try {

			CibnInfo info = getInfo(ctx.openFileInput("cibnactivate.xml"));
			if (info == null || info.id.length() <= 0) {
				ctx.deleteFile("cibnactivate.xml");
				return result;
			}
			final String url = LURL + "deviceId=" + info.id + "&mac="
					+ fileName;
			LogUtil.v(TAG, "******* CibnLaunche!!! url " + url);

			final HttpGet get = mHttpApi.createHttpGet(url);

			final HttpResponse resp = mHttpApi.executeHttpRequest(get);
			final int statusCode = resp.getStatusLine().getStatusCode();
			LogUtil.v(TAG, "statusCode is " + statusCode);
			switch (statusCode) {
			case 200:
				// str = CommonUtil.getStringFromInputStream(AbstractHttpApi
				// .getUngzippedContent(resp.getEntity()));
				Log.v(TAG, "###########  CibnInfo  begin");
				CibnInfo infola = getlaucherInfo(AbstractHttpApi
						.getUngzippedContent(resp.getEntity()));
				if (infola == null) {
					ctx.deleteFile("cibnactivate.xml");
					Log.v(TAG, "###########  CibnInfo  is null");
				} else {
					Log.v(TAG, "result: " + infola.laucherresult);
//					Toast.makeText(ctx,"result: " + infola.laucherresult + " state: " + infola.state, Toast.LENGTH_LONG).show();
					if (infola.state == 111) {
						result = true;
					} else {
						ctx.deleteFile("cibnactivate.xml");
						CIBNFALIR = "deviceID=" + info.id + "&mac=" + fileName;
					}
				}
				LogUtil.v(TAG, "******* CibnActivate!!! "
						+ infola.laucherresult + "  " + infola.state);

				break;
			case 304:
				break;
			case 403:
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public CibnInfo getInfo(InputStream instream) throws Exception {

		XmlPullParser parser = Xml.newPullParser();

		parser.setInput(instream, "UTF-8");
		int eventType = parser.getEventType();
		CibnInfo info = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case (XmlPullParser.START_DOCUMENT):
				break;
			case (XmlPullParser.START_TAG):
				String tagName = parser.getName();
				if ("online".equals(tagName)) {
					LogUtil.v(TAG, "******* new a info!");
					info = new CibnInfo();
				}
				if (info != null) {
					if ("deviceId".equals(tagName))
					{
						info.id = new String(parser.nextText());
						LogUtil.v(TAG, "******* info id!" + info.id);
					} else if ("resultCode".equals(tagName))
					{
						info.result = new String(parser.nextText());
						LogUtil.v(TAG, "******* info resultCode!" + info.result);
					}
				}
				break;
			case (XmlPullParser.END_TAG):
				if ("online".equals(parser.getName())) {
					LogUtil.v(TAG, "xml nod end");
				}
				break;
			}
			eventType = parser.next();
		}
		return info;
	}

	public CibnInfo getlaucherInfo(InputStream instream) throws Exception {

		XmlPullParser parser = Xml.newPullParser();

		parser.setInput(instream, "UTF-8");
		int eventType = parser.getEventType();
		CibnInfo info = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case (XmlPullParser.START_DOCUMENT):
				break;
			case (XmlPullParser.START_TAG):
				String tagName = parser.getName();
				if ("online".equals(tagName)) {
					LogUtil.v(TAG, "******* new a info!");
					info = new CibnInfo();
				}
				if (info != null) {
					if ("resultCode".equals(tagName))
					{
						info.laucherresult = new Integer(parser.nextText());
						LogUtil.v(TAG, "******* info laucherid!"
								+ info.laucherresult);
					} else if ("state".equals(tagName))
					{
						info.state = new Integer(parser.nextText());
						LogUtil.v(TAG, "******* info state!" + info.state);
					}
				}
				break;
			case (XmlPullParser.END_TAG):
				if ("online".equals(parser.getName())) {
					LogUtil.v(TAG, "xml nod end");
					return info;
				}
				break;
			}
			eventType = parser.next();
		}
		return info;
	}

	public class CibnInfo {
		public CibnInfo() {

		}

		public String id;
		public String result;
		public int laucherresult;
		public int state;
	}

}
