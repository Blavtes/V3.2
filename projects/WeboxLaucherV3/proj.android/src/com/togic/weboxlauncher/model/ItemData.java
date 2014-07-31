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

import android.R.string;
import android.os.Parcel;

import com.togic.weboxlauncher.util.CommonUtil;

/**
 * @author mountains.liu@togic.com @date 2014-03-10
 */
public abstract class ItemData extends Data {

    public interface LinkListener {
        void onLinkTo(ItemData data);
    }

    public int width;
    public int height;

    public String action;
    public String packageName;
    public String className;
    public String title;
    public int isshow;
    public int    visible;

    public LinkListener mLinkListener;

    public ItemData() {
        width = 0;
        height = 0;
        action = null;
        packageName = null;
        className = null;
        title = null;
        isshow = 0;
        visible = 0;
    }

    protected ItemData(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        action = in.readString();
        packageName = in.readString();
        className = in.readString();
        title = in.readString();
        visible = in.readInt();
        isshow = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(action);
        dest.writeString(packageName);
        dest.writeString(className);
        dest.writeString(title);
        dest.writeInt(visible);
        dest.writeInt(isshow);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CombData) {
            final ItemData i = (ItemData) o;
            return width == i.width && height == i.height
                    && CommonUtil.isEqualsString(packageName, i.packageName)
                    && CommonUtil.isEqualsString(className, i.className)
                    && CommonUtil.isEqualsString(title, i.title)
                    && isshow == i.isshow;
        }
        return false;
    }

    public boolean isValid() {
        return width <= 0 && height <= 0;
    }

    public void scaleLayoutParams() {
        if (isValid()) {
            width = CommonUtil.scalePixels(width);
            height = CommonUtil.scalePixels(height);
        }
    }

    public void setLinkListener(LinkListener l) {
        mLinkListener = l;
    }
}
