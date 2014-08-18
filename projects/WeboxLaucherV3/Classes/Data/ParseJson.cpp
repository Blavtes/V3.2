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
	//.................
}

ParseJson::~ParseJson()
{
	//.................
}

 bool  ParseJson::getItemVectorFromJSON(std::string jsonString,Vector<ItemData*>& itemVector)
{
	 std::string jsonContent =jsonString;
	 if (FileUtils::getInstance()->isFileExist(jsonString))
	 {
		 jsonContent = FileUtils::getInstance()->getStringFromFile(jsonString);
	 }
	 log("jsonString content is : %s",jsonContent.c_str());

	  rapidjson::Document doc;
	  doc.Parse<0>(jsonContent.c_str());
	  if (doc.HasParseError())
	  {
	       log("getParseError %s \n",doc.GetParseError());
	       return false;
	  }
	  const rapidjson::Value& items = doc["items"];
	  if (items.IsArray())
	  {
	      for (unsigned int i = 0;  i < items.Size(); i++)
	      {
	      	const rapidjson::Value& jsonItem = items[i];
	      	ItemData* itemData = ItemData::create(jsonItem);
//	          if(items[i].IsArray())
//	          {
//	          	log("@xjx--------the number of items[%d] %d",i,items[i].Size());
//	          }
//	          else
//	          {
//	          	for(auto iter = items[i].MemberonBegin(); iter !=items[i].MemberonEnd(); iter++)
//	          	 {
//	          	          log("@xjx-----member name:%s",iter->name.GetString());
////	           	          log("@xjx-----member value:%s",iter->value.GetString());
//	          	 }
//	          }
	          itemVector.pushBack(itemData);
	      }
	  }
	 return true;
}


