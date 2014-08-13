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

#include "ParseJson.h"
#include "json/rapidjson.h"
#include "json/document.h"
#include <vector>
#include "tinyxml2/tinyxml2.h"

USING_NS_CC;
using namespace std;

ParseJson::ParseJson()
{
    m_itemDataVector = new Vector<ItemData*>();
}

ParseJson::~ParseJson()
{
    delete m_itemDataVector;
}

ParseJson* ParseJson::create(std::string jsonPath)
{
    ParseJson *parse = new ParseJson();
    if (parse && parse->init(jsonPath)) {
        return parse;
    }
    delete parse;
    return nullptr;
}

bool ParseJson::init(std::string jsonPath)
{
    std::string strings = "";
    if (FileUtils::getInstance()->isFileExist(jsonPath)) {
         strings = FileUtils::getInstance()->getStringFromFile(jsonPath);
    } else {
        strings = jsonPath;
    }

    CCLOG("json %s",strings.c_str());

    rapidjson::Document doc;
    doc.Parse<0>(strings.c_str());
    if (doc.HasParseError()) {
        CCLOG("getParseError %s \n",doc.GetParseError());
        return false;
    }

    const rapidjson::Value& items = doc["items"];
    if (items.IsArray()) {
        for (unsigned int i = 0;  i < items.Size(); i++)
        {
            ItemData* itemData = ItemData::create();
            const rapidjson::Value &parmas = items[i]["parmas"];
            std::vector<Map<std::string, Ref*>> arr ;
            if (parmas.IsArray())
            {
                for (unsigned int j = 0; j < parmas.Size(); j++)
                {
                    Map<std::string, Ref*> map0 =  Map<std::string, Ref*>();
                    const rapidjson::Value &action = parmas[j]["action"];
                    log("j _->  %s",action.GetString());
                    const rapidjson::Value &keys = parmas[j]["key"];
                    log("j _->  %d",keys.GetBool());
                    Ref *f = __String::create(action.GetString());
                    map0.insert("action",  f);
                    Value valu(keys.GetBool());
                    Ref * k = __String::create(valu.getDescription());
                    map0.insert("key", k);
                    arr.push_back(map0);
                }
                if (arr.size() > 0)
                {
                    itemData->setParmas(arr);
                }
            }
            else
            {
                log("/// s %s ",parmas.GetString());
            }
            const rapidjson::Value &width = items[i]["width"];
            if (width.IsInt())
            {
                itemData->setWidth(width.GetInt());
            }

            const rapidjson::Value &height = items[i]["height"];
            if (height.IsInt())
            {
                itemData->setHeight(height.GetInt());
            }

            const rapidjson::Value &focuseScale = items[i]["focusScale"];
            if (focuseScale.IsDouble())
            {
                itemData->setFocusScale((float)focuseScale.GetDouble());
                CCLOG("//// focuse //// %f",itemData->getFocusScale());
            }

            const rapidjson::Value &notifyTitle = items[i]["notifyTitle"];
            if (notifyTitle.IsString())
            {
                itemData->setHintText(notifyTitle.GetString());
            }

            const rapidjson::Value &action = items[i]["action"];
            if (action.IsString()) {
                itemData->setAction(action.GetString());
            }

            const rapidjson::Value &cls = items[i]["class"];
            if (cls.IsString())
            {
                itemData->setClass(cls.GetString());
            }
            const rapidjson::Value &package = items[i]["package"];
            if (package.IsString())
            {
                itemData->setPackage(package.GetString());
            }
            const rapidjson::Value &gravity = items[i]["gravity"];
            if (gravity.IsInt()) {
                itemData->setGravity(gravity.GetInt());
            }

            const rapidjson::Value &clipping = items[i]["clipping"];
            if (clipping.IsBool())
            {
                itemData->setClipping(clipping.GetBool());
            }

            const rapidjson::Value &background = items[i]["background"];
            if (background.IsString())
            {
                itemData->setBackgroundImageFilePath(background.GetString());
            }
            const rapidjson::Value &bottom = items[i]["bottom_bg"];
            if (bottom.IsString())
            {
                itemData->setBottomBackGroundImageFilePath(bottom.GetString());
            }

            const rapidjson::Value &icon = items[i]["icon"];
            if (icon.IsString())
            {
                itemData->setForegroundImageFilePath(icon.GetString());
            }

            const rapidjson::Value &isshow = items[i]["isshow"];
            if (isshow.GetBool())
            {
                itemData->setIsShow(isshow.GetBool());
            }

            const rapidjson::Value &ID = items[i]["_id"];
            if (ID.IsInt())
            {
                itemData->setID(ID.GetInt());
            }
            
            m_itemDataVector->pushBack(itemData);
        }
    }
        return true;
}


Vector<ItemData*> ParseJson::getItemDataVectot()
{
    return *m_itemDataVector;
}
