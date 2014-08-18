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

import android.os.Parcel;
import android.os.Parcelable;

import com.togic.weboxlauncher.util.CommonUtil;

/**
 * @author mountains.liu@togic.com @date 2014-3-10
 */
public class CombData extends ItemData {

    // tv(20), video(21), history(22), album(23)
    public int id;

    // center(1), left|bottom(2), right|bottom(3), center|bottom(4)
    public int gravity;

    public boolean clip;

    public String background;
    public String bottomBg;
    public String icon;
    public String title;
    public int isshow;
    public String category_tag;
    // public String label;
    // public String hint;

    public CombData() {}

    protected CombData(Parcel in) {
        super(in);

        id = in.readInt();
        gravity = in.readInt();
        clip = in.readInt() != 0;

        background = in.readString();
        bottomBg = in.readString();
        icon = in.readString();
        title = in.readString();
        isshow = in.readInt();
        category_tag = in.readString();
        // label = in.readString();
        // hint = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(id);
        dest.writeInt(gravity);
        dest.writeInt(clip ? 1 : 0);

        dest.writeString(background);
        dest.writeString(bottomBg);
        dest.writeString(icon);
        dest.writeString(title);
        dest.writeInt(isshow);
        dest.writeString(category_tag);
        // dest.writeString(label);
        // dest.writeString(hint);
    }

    public boolean equals(Object o) {
        if (o instanceof CombData) {
            final CombData c = (CombData) o;
            return super.equals(o) && id == c.id && gravity == c.gravity
                    && CommonUtil.isEqualsString(background, c.background)
                    && CommonUtil.isEqualsString(bottomBg, c.bottomBg)
                    && CommonUtil.isEqualsString(icon, c.icon)
                    && CommonUtil.isEqualsString(title, c.title)
                    && isshow == c.isshow && CommonUtil.isEqualsString(category_tag, c.category_tag);
            // && CommonUtil.isEqualsString(label, c.label)
            // && CommonUtil.isEqualsString(hint, c.hint);
        }
        return false;
    }

    @Override
    public String toString() {
        return "id=" + id + "  w=" + width + "  h=" + height + "  action="
                + action + "\npkg/cls=" + packageName + "/" + className
                + "  gravity=" + gravity + "  clip=" + clip + "\nbg="
                + background + "  bottomBg=" + bottomBg + "  icon=" + icon + "  title=" + title + " isshow=" + isshow + "category="+ category_tag;
    }

    public int describeContents() {
        return DESCRIBE_COMB;
    }

    @Override
    public String[] needDownloadUrls() {
        return new String[] {
                background, bottomBg, icon
        };
    }

    @Override
    public boolean fixDownloadUrls(String[] urls) {
        if (super.fixDownloadUrls(urls)) {
            background = urls[0];
            bottomBg = urls[1];
            icon = urls[2];
            return true;
        }

        return false;
    }

    public static final Parcelable.Creator<CombData> CREATOR = new Parcelable.Creator<CombData>() {
        public CombData createFromParcel(Parcel in) {
            return new CombData(in);
        }

        public CombData[] newArray(int size) {
            return new CombData[size];
        }
    };
}
