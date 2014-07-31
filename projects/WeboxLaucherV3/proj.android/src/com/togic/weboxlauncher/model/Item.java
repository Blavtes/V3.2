/**
 * Copyright (C) 2012 Togic Corporation. All rights reserved.
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

/**
 * @author mountains.liu@togic.com @date 2014-03-10
 */
public class Item extends Data {

    private static final String TAG = "Item";

    public int category = INVALID_DATA;
    public ItemData data;

    public Item(ItemData d) {
        category = Data.getItemCategory(d);
        data = d;
    }

    protected Item(Parcel in) {
        category = in.readInt();
        if (category != INVALID_DATA) {
            data = Data.createItemData(in, category);
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category);
        if (category != INVALID_DATA) {
            data.writeToParcel(dest, flags);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Item) {
            final Item i = (Item) o;
            if (category != i.category) {
                return false;
            } else if (data == null) {
                return i.data == null;
            } else {
                return data.equals(i.data);
            }
        }
        return false;
    }

    public boolean isValid() {
        return category != INVALID_DATA && data != null && data.isValid();
    }

    public int describeContents() {
        return DESCRIBE_ITEM;
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
