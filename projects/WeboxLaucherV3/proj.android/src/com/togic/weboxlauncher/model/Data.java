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

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.togic.remote.types.RemoteType;
import com.togic.weboxlauncher.util.CommonUtil;
import com.togic.weboxlauncher.util.LogUtil;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mountains.liu@togic.com @date 2014-03-10
 */
public abstract class Data implements Parcelable, Serializable, RemoteType {

    private static final String TAG = "Data";

    // used to parse data
    static final String ATTR_PAGES = "pages";
    static final String ATTR_PAGE = "page";
    static final String ATTR_ITEMS = "items";
    static final String ATTR_ITEM = "item";

    static final String ATTR_VERSION = "version";
    static final String ATTR_BACKGROUND = "background";

    static final String ATTR_ID = "_id";
    static final String ATTR_WIDTH = "width";
    static final String ATTR_HEIGHT = "height";
    static final String ATTR_VISIBLE = "visible";
    static final String ATTR_TITLE = "title";
    static final String ATTR_INTENT = "intent";
    static final String ATTR_INTENT_ACTION = "action";
    static final String ATTR_INTENT_PKG = "package";
    static final String ATTR_INTENT_CLS = "class";
    static final String ATTR_GRAVITY = "gravity";
    static final String ATTR_CLIP = "clip";
    static final String ATTR_BOTTOMBG = "bottom_bg";
    static final String ATTR_ICON = "icon";
    static final String ATTR_ISSHOW = "isshow";
    static final String ATTR_CATEGORY_TAG = "category_tag";

    static final int INVALID_DATA = -1;

    static final int DESCRIBE_PAGE = 0x1000;

    static final int DESCRIBE_ITEM = 0x2000;

    static final int DESCRIBE_COMB = 0x3000;

    static final JSONObject getJSONObject(JSONObject obj, String name) {
        if (obj != null) {
            return obj.optJSONObject(name);
        } else {
            return null;
        }
    }

    static final JSONArray getJSONArray(JSONObject obj, String name) {
        if (obj != null) {
            return obj.optJSONArray(name);
        } else {
            return null;
        }
    }

    static final boolean getBoolean(JSONObject obj, String name) {
        return getBoolean(obj, name, false);
    }

    static final boolean getBoolean(JSONObject obj, String name,
            boolean fallback) {
        if (obj != null) {
            return obj.optBoolean(name, fallback);
        } else {
            return fallback;
        }
    }

    static final int getInt(JSONObject obj, String name) {
        return getInt(obj, name, 0);
    }

    static final int getInt(JSONObject obj, String name, int fallback) {
        if (obj != null) {
            return obj.optInt(name, fallback);
        } else {
            return fallback;
        }
    }

    static final long getLong(JSONObject obj, String name) {
        return getLong(obj, name, 0);
    }

    static final long getLong(JSONObject obj, String name, long fallback) {
        if (obj != null) {
            return obj.optLong(name, fallback);
        } else {
            return fallback;
        }
    }

    static final String getString(JSONObject obj, String name) {
        return getString(obj, name, "");
    }

    static final String getString(JSONObject obj, String name, String fallback) {
        if (obj != null) {
            return obj.optString(name, fallback);
        } else {
            return fallback;
        }
    }

    static final Intent getIntent(JSONObject obj) {
        final Intent intent = new Intent();
        if (obj != null) {
            final String action = getString(obj, ATTR_INTENT_ACTION, null);
            if (action != null) {
                intent.setAction(action);
            }

            final String pkg = getString(obj, ATTR_INTENT_PKG, null);
            final String cls = getString(obj, ATTR_INTENT_CLS, null);
            if (pkg != null && cls != null) {
                intent.setClassName(pkg, cls);
            }
        }

        return intent;
    }

    static final ItemData createItemData(Parcel in, int category) {
        switch (category) {
        case DESCRIBE_COMB:
            return CombData.CREATOR.createFromParcel(in);
        default:
            LogUtil.i(TAG, "Unsupport category: " + category);
            return null;
        }
    }

    static final int getItemCategory(ItemData data) {
        if (data == null) {
            return INVALID_DATA;
        } else {
            return data.describeContents();
        }
    }

    // NOTE: don't return null
    public String[] needDownloadUrls() {
        return new String[] {};
    }

    public boolean fixDownloadUrls(String[] urls) {
        if (CommonUtil.getArrayLength(urls) >= CommonUtil
                .getArrayLength(needDownloadUrls())) {
            return true;
        } else {
            return false;
        }
    }
}
