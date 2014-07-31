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

package com.togic.weboxlauncher.app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mts @date 2014年1月10日
 */
public class AppFilter implements Filter<AppInfo> {

    private ArrayList<String> mIncludePkgs = new ArrayList<String>();
    private ArrayList<String> mExcludePkgs = new ArrayList<String>();

    private final String mFilterKey;
    private final int mFilteFlags;

    public AppFilter(String key, int flag, List<String> include,
            List<String> exclude) {
        mFilterKey = key;
        mFilteFlags = flag & FLAG_MASK;

        if (include != null) {
            mIncludePkgs.addAll(include);
        }

        if (exclude != null) {
            mExcludePkgs.addAll(exclude);
        }
    }

    @Override
    public String getFilterKey() {
        return mFilterKey;
    }

    @Override
    public List<AppInfo> filter(List<AppInfo> apps) {
        if (apps == null || apps.size() == 0) {
            return new ArrayList<AppInfo>(0);
        }

        final boolean includeFilter = (mFilteFlags & FLAG_INCLUDE) != 0;
        final boolean excludeFilter = (mFilteFlags & FLAG_EXCLUDE) != 0;

        final int size = apps.size();
        final ArrayList<AppInfo> result = new ArrayList<AppInfo>(size);

        AppInfo temp = null;
        if (includeFilter) {
            final ArrayList<String> include = mIncludePkgs;
            for (int i = size - 1; i >= 0; i--) {
                temp = apps.get(i);
                if (include.contains(temp.getPackageName())) {
                    result.add(temp);
                }
            }
        } else {
            result.addAll(apps);
        }

        if (excludeFilter) {
            final ArrayList<String> exclude = mExcludePkgs;
            for (int i = result.size() - 1; i >= 0; i--) {
                temp = result.get(i);
                if (exclude.contains(temp.getPackageName())) {
                    result.remove(i);
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "AppFilter: {\n" + "KEY=" + mFilterKey + " FLAG=" + mFilteFlags
                + "\nINCLUDE=" + mIncludePkgs + "\nEXCLUDE=" + mExcludePkgs
                + "\n}";
    }
}
