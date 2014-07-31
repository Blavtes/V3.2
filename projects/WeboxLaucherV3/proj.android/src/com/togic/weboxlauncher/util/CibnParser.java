package com.togic.weboxlauncher.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.togic.remote.http.AbstractHttpApi;
import com.togic.weboxlauncher.http.CibnApi;

public class CibnParser {

	private static final String TAG = "cibnapi";
	public interface CibnCallback {
		public void onCheckBegin();

		public void onCheckEnd(boolean result);
	}

	public static final void parse(final Context ctx, CibnCallback callback) {
		LogUtil.v(TAG, "---------------------\r\n begin cibn check!");
		callback.onCheckBegin();

		final CibnApi api = CibnApi.getInstance();
		FileInputStream in = null;
		try {
			in = ctx.openFileInput("cibnactivate.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			in = null;
		}
		if (in == null) {
			boolean str = api.CibnActivate(ctx, getLocalMacAddress());
			LogUtil.v(TAG, "cibn init res: " + str);
		} else {
			LogUtil.v(TAG, "\r\n" + "cibn: the device has Inited !");
		}
		boolean result = api.CibnLaunche(ctx, getLocalMacAddress());
		callback.onCheckEnd(result);
		LogUtil.v(TAG, "---------------------\r\nLogin"
				+ (result ? "OK!" : "ERR!"));
		if (!result) {
			JSONArray array = new JSONArray();
			 JSONObject item = new JSONObject();
			 try {
				 item.put("cibn_Failure_MacID", getLocalMacAddress());
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }
			 array.put(item);
			 String string = postStringToServer("http://statistics.togic.com:10086/weboxLauncher",array.toString());
			 Log.v(TAG, "//////deviID   " + string + "arr :" + array.toString());
		}
	}
	
	public static String postStringToServer(String url, String content) {
		InputStream is = null;
		HttpClient httpClient = AbstractHttpApi.createHttpClient();
		try {
			HttpPost httpPost = null;
			if (url != null) {
				httpPost = new HttpPost(url);
				Log.v(TAG, "webox url - " + httpPost.getURI());
			}
			StringEntity strEntity = new StringEntity(content, HTTP.UTF_8);
			strEntity.setContentType("application/json");
			httpPost.setEntity(strEntity);
			HttpResponse response = httpClient.execute(httpPost);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				HttpEntity entity = response.getEntity();
				is = AbstractHttpApi.getUngzippedContent(entity);
				Log.v(TAG, "post string to server success");
				return "success";
			} else if (responseCode == 403) {
				Log.v(TAG, " responseCode 403");
				// SSLToken.checkSSLTokenValid(response.getEntity(),
				// Launcher.sCtx);
			} else {
				response.getEntity().consumeContent();
				// Log.v(TAG, " responseCode " +
				// response.getEntity().consumeContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.v(TAG, "post string to server failed");

		return "failed";
	}

	public static void PostFunction(String key, HashMap<String, String> map) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(
				"http://statistics.togic.com:10086/weboxLauncher");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

			nameValuePairs.add(new BasicNameValuePair(key, map.toString()));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response;
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, "MyFunction clickde post..." + map.toString());
	}

	public static String getLocalMacAddress() {
		String Mac = null;
		try {

			String path = "sys/class/net/eth0/address";

			if (Mac == null || Mac.length() == 0) {
				path = "sys/class/net/eth0/address";
				FileInputStream fis_name = new FileInputStream(path);
				byte[] buffer_name = new byte[8192];
				int byteCount_name = fis_name.read(buffer_name);
				if (byteCount_name > 0) {
					Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
				}
			}
			Log.v(TAG, "" + Mac);
			if (Mac.length() == 0 || Mac == null) {
				return "";
			}
		} catch (Exception io) {
			Log.v("TAG", "" + io.toString());
		}

		Log.v("TAG", Mac);
		return Mac.trim();
	}
}
