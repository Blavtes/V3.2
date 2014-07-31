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

package com.togic.weboxlauncher.backend;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author mts @date 2013年10月25日
 */
@SuppressLint("HandlerLeak")
public abstract class BaseManager {

    public void create() {
        createTaskHandler();
    }

    public void destroy() {
        destroyTaskHandler();
    }

    protected boolean hasTask(int msg) {
        return hasTask(getTaskHandler(), msg);
    }

    protected boolean hasTask(Handler h, int msg) {
        return h.hasMessages(msg);
    }

    protected void scheduleTask(int msg, long delay) {
        scheduleTask(getTaskHandler(), msg, delay);
    }

    protected void scheduleTask(Handler h, int msg, long delay) {
        LogUtil.v(getTag(), "&&&&&&&&&&&&&&&&&&&&& scheduleTask: " + h + "  "
                + getMessage(msg) + "  " + delay);
        h.removeMessages(msg);
        h.sendEmptyMessageDelayed(msg, delay);
    }

    protected void unscheduleTask(int msg) {
        unscheduleTask(getTaskHandler(), msg);
    }

    protected void unscheduleTask(Handler h, int msg) {
        LogUtil.v(getTag(), "&&&&&&&&&&&&&&&&&&&&& unscheduleTask: " + h + "  "
                + getMessage(msg));
        h.removeMessages(msg);
    }

    protected abstract void createTaskHandler();

    protected abstract void destroyTaskHandler();

    protected abstract Handler getTaskHandler();

    protected abstract String getTag();

    protected abstract String getMessage(int msg);

    protected class TaskHandler extends Handler {

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            LogUtil.v(getTag(), "&&&&&&&&&&&&&&&&&&&&& handleMessage: "
                    + getMessage(msg.what));
        }
    }
}
