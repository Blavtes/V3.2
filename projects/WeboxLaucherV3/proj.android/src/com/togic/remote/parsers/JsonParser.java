/*
 * Copyright (C) 2011 Togic Corporation. All rights reserved.
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

package com.togic.remote.parsers;

import com.togic.remote.error.RemoteError;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.types.RemoteType;

/**
 * @author libin
 * @date 2012-9-3
 **/
public interface JsonParser<T extends RemoteType> {
    /**
     * 
     * @param json: json is JSONObject or JSONArray.
     * @return
     * @throws RemoteError
     * @throws RemoteParseException
     */
    public abstract T parse(Object json) throws RemoteError,
            RemoteParseException;
}
