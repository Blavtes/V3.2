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

package com.togic.weboxlauncher.app;

import java.util.HashMap;

import android.content.ComponentName;

/**
 * @author mountains.liu@togic.com @date 2012-11-8
 */
public class AppInfos extends HashMap<ComponentName, AppInfo> {

    public AppInfos(int capacity) {
        super(capacity);
    }

    public AppInfo put(ComponentName key, AppInfo value) {
        return super.put(key, value);
    }

    public AppInfos get(String pkgName) {
        if (pkgName == null || pkgName.length() == 0) {
            return null;
        }

        final AppInfos temp = new AppInfos(2);
        AppInfo app;
        for (ComponentName cpn : keySet()) {
            app = get(cpn);
            if (pkgName.equals(cpn.getPackageName())) {
                temp.put(cpn, app);
            }
        }

        return temp;
    }

    public AppInfos remove(String pkgName) {
        if (pkgName == null || pkgName.length() == 0) {
            return null;
        }

        final AppInfos temp = new AppInfos(2);
        for (ComponentName cpn : keySet()) {
            if (pkgName.equals(cpn.getPackageName())) {
                temp.put(cpn, remove(cpn));
            }
        }

        return temp;
    }

    public boolean contains(String pkgName) {
        if (pkgName == null || pkgName.length() == 0) {
            return false;
        }

        for (ComponentName cpn : keySet()) {
            if (pkgName.equals(cpn.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
