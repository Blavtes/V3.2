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

package com.togic.remote.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.togic.remote.error.RemoteCredentialsException;
import com.togic.remote.error.RemoteException;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.parsers.AbstractJsonParser;
import com.togic.remote.parsers.JsonParser;
import com.togic.remote.types.RemoteType;
import com.togic.weboxlauncher.App;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author libin
 */
abstract public class AbstractHttpApi implements HttpApi {

    private static final String TAG = AbstractHttpApi.class.getCanonicalName();

    protected static final Logger LOG = Logger.getLogger(TAG);

    protected static final boolean DEBUG = App.DEBUG;

    private static final String DEFAULT_CLIENT_VERSION = "com.togic.remote";
    private static final String CLIENT_VERSION_HEADER = "User-Agent";

    private static final int TIMEOUT = 12;
    private static final int RETRY_COUNT = 3;

    private final DefaultHttpClient mHttpClient;
    private final String mClientVersion;

    public AbstractHttpApi(DefaultHttpClient httpClient, String clientVersion) {
        mHttpClient = httpClient;
        if (clientVersion != null) {
            mClientVersion = clientVersion;
        } else {
            mClientVersion = DEFAULT_CLIENT_VERSION;
        }
    }

    public RemoteType executeHttpRequest(HttpRequestBase httpRequest,
            JsonParser<? extends RemoteType> parser)
            throws RemoteCredentialsException, RemoteParseException,
            RemoteException, IOException {
        if (DEBUG) {
            Log.d(TAG, "doHttpRequest: " + httpRequest.getURI());
        }

        final HttpResponse response = executeHttpRequest(httpRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
        case 200:
            InputStream is = getUngzippedContent(response.getEntity());
            final String cacheStr = CommonUtil.getStringFromInputStream(is);
            return parser.parse(AbstractJsonParser.parseObjBuilder(cacheStr));

        case 401:
        case 403:
        case 404:
        case 500:
            response.getEntity().consumeContent();
            throw new RemoteException(response.getStatusLine().toString());

        default:
            response.getEntity().consumeContent();
            throw new RemoteException("Error connecting to Foursquare: "
                    + statusCode + ". Try again later.");
        }
    }

    public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws RemoteCredentialsException, RemoteParseException,
            RemoteException, IOException {
        final HttpPost httpPost = createHttpPost(url, nameValuePairs);
        if (DEBUG) {
            Log.d(TAG, "doHttpPost: " + httpPost.getURI());
        }

        final HttpResponse response = executeHttpRequest(httpPost);
        final int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
        case 200:
            return EntityUtils.toString(response.getEntity());

        case 401:
        case 403:
        case 404:
            response.getEntity().consumeContent();
            throw new RemoteException(response.getStatusLine().toString());
        default:
            response.getEntity().consumeContent();
            throw new RemoteException("Error connecting to Foursquare: "
                    + statusCode + ". Try again later.");
        }
    }

    public static InputStream getUngzippedContent(HttpEntity entity)
            throws IOException {
        InputStream responseStream = entity.getContent();
        if (responseStream == null) {
            return null;
        }

        final Header header = entity.getContentEncoding();
        if (header == null) {
            return responseStream;
        }

        final String contentEncoding = header.getValue();
        if (contentEncoding == null) {
            return responseStream;
        }

        if (contentEncoding.contains("gzip")) {
            responseStream = new GZIPInputStream(responseStream);
        }
        return responseStream;
    }

    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     * 
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
            throws IOException {
        if (DEBUG) {
            LogUtil.v(TAG, "executing HttpRequest for: " + httpRequest.getURI());
        }

        try {
            mHttpClient.getConnectionManager().closeExpiredConnections();
            return mHttpClient.execute(httpRequest);
        } catch (IOException e) {
            httpRequest.abort();
            throw e;
        } catch (Exception e) {
            httpRequest.abort();
            throw new IOException();
        }
    }

    public HttpGet createHttpGet(String url, Map<String, Object> map) {
        NameValuePair[] arrays = null;
        if (map != null) {
            List<NameValuePair> nameVaulePairs = new ArrayList<NameValuePair>();
            for (String key : map.keySet()) {
                final Object value = map.get(key);
                if (value instanceof String) {
                    nameVaulePairs.add(new BasicNameValuePair(key,
                            (String) value));
                } else if (value instanceof String[]) {
                    String[] values = (String[]) value;
                    final int size = values.length;
                    for (int i = 0; i < size; i++) {
                        nameVaulePairs.add(new BasicNameValuePair(key,
                                values[i]));
                    }
                }
            }
            arrays = nameVaulePairs.toArray(new NameValuePair[] {});
        }

        return createHttpGet(url, arrays);
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        HttpGet httpGet = null;
        if (url != null) {
            String query = URLEncodedUtils.format(
                    stripNullsAndAddExtras(nameValuePairs), HTTP.UTF_8);
            httpGet = new HttpGet(url + (hasParamsInUrl(url) ? "&" : "?")
                    + query);
            httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
            httpGet.addHeader("Accept-Encoding", "gzip");
        }

        if (DEBUG) {
            LogUtil.v(TAG, "createHttpGet: " + httpGet);
        }
        return httpGet;
    }

    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        httpPost.addHeader("Accept-Encoding", "gzip");
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(
                    stripNullsAndAddExtras(nameValuePairs), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(
                    "Unable to encode http parameters.");
        }

        if (DEBUG) {
            LogUtil.v(TAG, "createHttpPost: " + httpPost);
        }
        return httpPost;
    }

    private List<NameValuePair> stripNullsAndAddExtras(
            NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("package", "com.togic.weboxlauncher"));
        params.add(new BasicNameValuePair("versionCode", "1"));

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

    private boolean hasParamsInUrl(String url) {
        return url.contains("?");
    }

    /**
     * Create a thread-safe client. This client does not do redirecting, to
     * allow us to capture correct "error" codes.
     * 
     * @return HttpClient
     */
    public static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));

        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                httpParams, supportedSchemes);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(ccm,
                httpParams);
        defaultHttpClient
                .setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
                        RETRY_COUNT, true));
        return defaultHttpClient;
    }

    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }
}
