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

/**
 * @author mts @date 2014年1月10日
 */
public interface AppWatcher {
    public void onAttached();

    public void onUpdateAllApps(int version, String id,
            ArrayList<AppInfo> apps);

    public void onUpdateApps(int version, String id,
            ArrayList<AppInfo> removed, ArrayList<AppInfo> updated);

    public void onDetached();
}