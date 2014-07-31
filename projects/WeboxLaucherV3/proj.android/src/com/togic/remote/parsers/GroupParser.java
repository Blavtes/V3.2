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

import org.json.JSONArray;

import com.togic.remote.error.RemoteError;
import com.togic.remote.error.RemoteParseException;
import com.togic.remote.types.Group;
import com.togic.remote.types.RemoteType;

/**
 * @author libin
 * @date 2012-9-3
 **/
public class GroupParser extends
        AbstractJsonParser<Group<? extends RemoteType>> {

    private JsonParser<? extends RemoteType> mSubParser;

    public GroupParser(JsonParser<? extends RemoteType> subParser) {
        this.mSubParser = subParser;
    }

    @Override
    public Group<? extends RemoteType> parse(Object json) throws RemoteError,
            RemoteParseException {
        JSONArray jsonArray;
        if (json instanceof JSONArray) {
            jsonArray = (JSONArray) json;
        } else {
            throw new RemoteParseException("not JSONArray instance");
        }

        Group<RemoteType> group = new Group<RemoteType>();
        for (int i = 0; i < jsonArray.length(); i++) {
            group.add(mSubParser.parse(jsonArray.optJSONObject(i)));
        }
        return group;
    }
}
