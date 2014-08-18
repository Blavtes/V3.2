/**
 * Copyright (C) 2013 Togic Corporation. All rights reserved.
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

package com.togic.weboxlauncher.app;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Comparator;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.togic.weboxlauncher.R;

/**
 * @author mountains.liu@togic.com @date 2013-1-21
 */
public class AppInfo {

    public static final Comparator<AppInfo> sLableComparator = new Comparator<AppInfo>() {
        public int compare(AppInfo app1, AppInfo app2) {
            return app1.label.toString().compareTo(app2.label.toString());
        }
    };

    private static Canvas sCanvas = new Canvas();
    private static int sIconWidth = -1;
    private static int sIconHeight = -1;

    public String label = "";
    public byte[] icon = null;
    public int iconWidth = -1;
    public int iconHeight = -1;
    public int proFlag = 1;
    public ComponentName cpnName = null;

    public String getClassName() {
        return cpnName != null ? cpnName.getClassName() : "";
    }

    public String getPackageName() {
        return cpnName != null ? cpnName.getPackageName() : "";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AppInfo) {
            return cpnName != null && cpnName.equals(((AppInfo) o).cpnName);
        }
        return false;
    }

    public String toString() {
        return "title:" + label + " cpnName:" + cpnName + "  iconW:"
                + iconWidth + "  iconH:" + iconHeight;
    }

    public static Bitmap createThumbnail(Context context, Drawable ic) {
        if (sIconWidth < 1 || sIconHeight < 1) {
            sIconWidth = sIconHeight = (int) context.getResources()
                    .getDimension(R.dimen.IconSize);
        }

        final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = sCanvas;
        canvas.setBitmap(thumb);

        Rect boundsOld = ic.getBounds();
        ic.setBounds(0, 0, sIconWidth, sIconHeight);
        ic.draw(canvas);
        ic.setBounds(boundsOld);
        return thumb;
    }

    public static AppInfo createFromResolveInfo(Context context,
            PackageManager pm, ResolveInfo resolveInfo) {
        final AppInfo app = new AppInfo();
        app.label = resolveInfo.loadLabel(pm).toString();

        final Bitmap b = createThumbnail(context, resolveInfo.loadIcon(pm));
        app.icon = getPixels(b);
        app.iconWidth = b.getWidth();
        app.iconHeight = b.getHeight();
        b.recycle();

        return app;
    }

    public static byte[] getPixels(final Bitmap bitmap) {
        if (bitmap != null) {
            final byte[] pixels = new byte[bitmap.getWidth()
                    * bitmap.getHeight() * 4];
            final ByteBuffer buf = ByteBuffer.wrap(pixels);
            buf.order(ByteOrder.nativeOrder());
            bitmap.copyPixelsToBuffer(buf);
            return pixels;
        }

        return null;
    }
}
