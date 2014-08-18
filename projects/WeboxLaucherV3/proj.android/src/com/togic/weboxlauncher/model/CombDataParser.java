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

import android.app.ActionBar.Tab;
import android.nfc.Tag;
import android.util.Log;

import com.togic.remote.error.RemoteError;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.parsers.AbstractJsonParser;
import com.togic.weboxlauncher.util.CommonUtil;

/**
 * @author mts @date 2014���������������������������3���������������������������11���������������������������
 */
public class CombDataParser extends AbstractJsonParser<CombData> {

    @Override
    public CombData parse(Object json) throws RemoteError, RemoteParseException {
        if (json == null) {
            throw new RemoteParseException("json is null.");
        } else if (!(json instanceof JSONObject)) {
            throw new RemoteParseException("json is not a JSONObject.");
        }

        final JSONObject obj = (JSONObject) json;
        final CombData data = new CombData();

        data.id = Data.getInt(obj, Data.ATTR_ID);
        data.width = Data.getInt(obj, Data.ATTR_WIDTH);
        data.height = Data.getInt(obj, Data.ATTR_HEIGHT);
        data.visible = Data.getInt(obj, Data.ATTR_VISIBLE);
        data.title = Data.getString(obj, Data.ATTR_TITLE);
        data.action = Data.getString(obj, Data.ATTR_INTENT_ACTION);
        data.packageName = Data.getString(obj, Data.ATTR_INTENT_PKG);
        data.className = Data.getString(obj, Data.ATTR_INTENT_CLS);

        data.gravity = Data.getInt(obj, Data.ATTR_GRAVITY);
        data.clip = Data.getBoolean(obj, Data.ATTR_CLIP);
        data.background = CommonUtil.fullUrl(Data.getString(obj,
                Data.ATTR_BACKGROUND));
        data.bottomBg = CommonUtil.fullUrl(Data.getString(obj,
                Data.ATTR_BOTTOMBG));
        data.icon = CommonUtil.fullUrl(Data.getString(obj, Data.ATTR_ICON));
        data.isshow = Data.getInt(obj, Data.ATTR_ISSHOW);
        data.category_tag = Data.getString(obj, Data.ATTR_CATEGORY_TAG);
        Log.v("laucnher sdf ", "data  " + data.isshow);
        return data;
    }
}
