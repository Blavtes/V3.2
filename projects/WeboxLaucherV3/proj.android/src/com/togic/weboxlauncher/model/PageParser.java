/**
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

package com.togic.weboxlauncher.model;

import org.json.JSONObject;

import com.togic.remote.error.RemoteError;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.parsers.AbstractJsonParser;
import com.togic.remote.parsers.GroupParser;
import com.togic.remote.types.Group;
import com.togic.weboxlauncher.http.MetroApi;
import com.togic.weboxlauncher.util.CommonUtil;

/**
 * @author mts @date 2014年3月11日
 */
public class PageParser extends AbstractJsonParser<Page> {

    @SuppressWarnings("unchecked")
    @Override
    public Page parse(Object json) throws RemoteError, RemoteParseException {
        if (json == null) {
            throw new RemoteParseException("json is null.");
        } else if (!(json instanceof JSONObject)) {
            throw new RemoteParseException("json is not a JSONObject.");
        }

        final JSONObject obj = (JSONObject) json;
        final Page data = new Page();

        data.version = Data.getLong(obj, Data.ATTR_VERSION);
        data.background = CommonUtil.fullUrl(Data.getString(obj,
                Data.ATTR_BACKGROUND));
        data.setItems((Group<ItemData>) new GroupParser(new CombDataParser())
                .parse(Data.getJSONArray(obj, Data.ATTR_ITEMS)));
        return data;
    }
}
