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

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Vector;

import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.RemoteException;

import com.togic.weboxlauncher.util.LogUtil;

/**
 * @author mountains.liu@togic.com @date 2013-10-21
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class ICallbackList extends Vector {

    private static final String TAG = "ICallbackList";

    public ICallbackList() {
        super();
    }

    public synchronized ArrayList<IInterface> getCallbacks() {
        final ArrayList<IInterface> callbacks = new ArrayList<IInterface>(size());
        for (Object obj : this) {
            if (obj instanceof CallbackState) {
                callbacks.add(((CallbackState) obj).callback);
            }
        }

        return callbacks;
    }

    public synchronized boolean addCallback(IInterface obj) {
        if (obj == null) {
            return false;
        }

        CallbackState temp;
        for (int i = size() - 1; i >= 0; i--) {
            temp = (CallbackState) get(i);
            if (temp.callback.asBinder() == obj.asBinder()) {
                return false;
            }
        }

        temp = new CallbackState(obj);
        if (temp.link()) {
            return super.add(temp);
        } else {
            return false;
        }
    }

    public synchronized boolean removeCallback(IInterface obj) {
        if (obj == null) {
            return false;
        }

        boolean result = false;
        CallbackState temp;
        for (int i = size() - 1; i >= 0; i--) {
            temp = (CallbackState) get(i);
            if (temp.callback.asBinder() == obj.asBinder()) {
                super.remove(i);
                temp.unlink();
                result = true;
            }
        }

        return result;
    }

    public final class CallbackState implements DeathRecipient {

        private IInterface callback;

        public CallbackState(IInterface cbk) {
            callback = cbk;
        }

        @Override
        public void binderDied() {
            ICallbackList.this.removeCallback(callback);
        }

        @Override
        public String toString() {
            return "{ callback=" + callback + ", binder=" + callback.asBinder() + " }";
        }

        public boolean link() {
            try {
                callback.asBinder().linkToDeath(this, 0);
                LogUtil.v(TAG, "&&&&&&&& link to death: " + this);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return false;
        }

        public boolean unlink() {
            try {
                final boolean result = callback.asBinder().unlinkToDeath(this, 0);
                LogUtil.v(TAG, "&&&&&&&& unlink to death: " + this + ", result=" + result);
                return result;
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
