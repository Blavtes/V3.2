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

package com.togic.remote.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.togic.remote.error.RemoteError;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.types.RemoteType;

/**
 * @author libin
 * @date 2012-9-3
 **/
public abstract class AbstractJsonParser<T extends RemoteType> implements
        JsonParser<T> {

    public abstract T parse(Object json) throws RemoteError,
            RemoteParseException;

    protected List<String> parseJsonArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> parseJsonMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<String, String>();
        if (jsonObject == null) {
            return map;
        }
        Iterator<String> keys = jsonObject.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, jsonObject.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static final Object parseObjBuilder(String json)
            throws RemoteParseException {
        JSONTokener jsonTokener = new JSONTokener(json);
        try {
            return jsonTokener.nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RemoteParseException(e.getMessage());
        }
    }
}
