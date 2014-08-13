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

#ifndef __WeBoxLauncher__ParseJson__
#define __WeBoxLauncher__ParseJson__

#include "cocos2d.h"
#include "ItemData.h"

class ParseJson
{
public:
    ParseJson();
    ~ParseJson();
    /**
     * create: jsonpath is path or json string.
     *
     */

    static ParseJson* create(std::string jsonPath);
    Vector<ItemData*> getItemDataVectot();
    bool init(std::string jsonPath);
private:
    Vector<ItemData*> *m_itemDataVector;
};

#endif /* defined(__WeBoxLauncher__ParseJson__) */
