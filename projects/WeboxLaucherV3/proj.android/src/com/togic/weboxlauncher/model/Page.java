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

import java.util.ArrayList;
import java.util.List;

import com.togic.weboxlauncher.util.CommonUtil;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mountains.liu@togic.com @date 2014-03-10
 */
public class Page extends Data {

    public long version;
    public String background;
    private final ArrayList<Item> items = new ArrayList<Item>();

    public Page() {}

    private Page(Parcel in) {
        version = in.readLong();
        background = in.readString();
        in.readList(items, Item.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(version);
        dest.writeString(background);
        dest.writeList(items);
    }

    public final void addData(ItemData data) {
        addData(new Item(data));
    }

    public final void addData(Item item) {
        if (item != null && item.isValid()) {
            items.add(item);
        }
    }

    public final void setItems(List<ItemData> is) {
        items.clear();
        for (ItemData item : is) {
            items.add(new Item(item));
        }
    }

    public final List<ItemData> getItems() {
        final ArrayList<ItemData> datas = new ArrayList<ItemData>(items.size());
        for (Item item : items) {
            datas.add(item.data);
        }
        return datas;
    }

    public final void setPage(Page page) {
        if (page == this) {
            return;
        }

        background = page.background;
        refreshItems(page);
    }

    public final boolean isBackgroundEquals(Page page) {
        return CommonUtil.isEqualsString(background, page.background);
    }

    public final void refreshBackground(Page page) {
        background = page.background;
    }

    public final boolean isItemsEquals(Page page) {
        return items.equals(page.items);
    }

    public final void refreshItems(Page page) {
        items.clear();
        items.addAll(page.items);
    }

    public final void clear() {
        items.clear();
    }

    public boolean equals(Object o) {
        if (o instanceof Page) {
            final Page p = (Page) o;
            return CommonUtil.isEqualsString(background, p.background)
                    && items.equals(p.items);
        }
        return false;
    }

    public String toString() {
        return "ver=" + version + "  bg=" + background + "  items=" + items;
    }

    public final boolean isValid() {
        return !CommonUtil.isEmptyString(background) && items.size() != 0;
    }

    public void scalePage() {
        if (isValid()) {
            for (Item item : items) {
                item.data.scaleLayoutParams();
            }
        }
    }

    public int describeContents() {
        return DESCRIBE_PAGE;
    }

    @Override
    public String[] needDownloadUrls() {
        return new String[] {
            background
        };
    }

    @Override
    public boolean fixDownloadUrls(String[] urls) {
        if (super.fixDownloadUrls(urls)) {
            background = urls[0];
            return true;
        }
        return false;
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
