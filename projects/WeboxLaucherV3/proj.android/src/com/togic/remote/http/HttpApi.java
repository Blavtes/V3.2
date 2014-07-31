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

import com.togic.remote.error.RemoteCredentialsException;
import com.togic.remote.error.RemoteException;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.parsers.JsonParser;
import com.togic.remote.types.RemoteType;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * @author libin
 */
public interface HttpApi {

    abstract public RemoteType doHttpRequest(HttpRequestBase httpRequest,
            JsonParser<? extends RemoteType> parser) throws RemoteCredentialsException,
            RemoteParseException, RemoteException, IOException;

    abstract public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws RemoteCredentialsException, RemoteParseException, RemoteException,
            IOException;

    abstract public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs);

    abstract public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs);
}
