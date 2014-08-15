/*
 * ItemData.cpp
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#include "ItemData.h"

using namespace std;

ItemData::ItemData()
: m_focusScale(1.0)
, m_gravity(-1)
, m_package("")
, m_class("")
, m_action("")
, m_backgroundImageFilePath("")
, m_foregroundImageFilePath("")
, m_bottomBackGroundImageFilePath("")
, m_hintText("")
, m_Clipping(false)
, m_isShow(true)
, m_id(-1)
, m_height(0)
, m_width(0)
{
    m_parmas = new vector<Map<string, Ref*>>();
}

ItemData::~ItemData()
{
    delete m_parmas;
}


ItemData* ItemData::create()
{
    ItemData* itemData = new ItemData();
    if(itemData && itemData->init()){
        itemData->autorelease();
        return itemData;
    }
    CC_SAFE_DELETE(itemData);
    return NULL;
}


ItemData* ItemData::create(const rapidjson::Value& jsonItem)
{
	ItemData* itemData = new ItemData();
	if (itemData && itemData->init())
	{
		if (jsonItem.IsObject())
		{
			if (jsonItem.HasMember("width"))
			{
				itemData->setWidth(jsonItem["width"].GetInt());
			}
			if (jsonItem.HasMember("height"))
			{
				itemData->setHeight(jsonItem["height"].GetInt());
			}
			if (jsonItem.HasMember("backgroundImageFilePath"))
			{
				const rapidjson::Value &background = jsonItem["backgroundImageFilePath"];
				if (background.IsString())
				{
					itemData->setBackgroundImageFilePath(background.GetString());
				}
			}
			if (jsonItem.HasMember("foregroundImageFilePath"))
			{
				itemData->setForegroundImageFilePath(jsonItem["foregroundImageFilePath"].GetString());
			}
			if (jsonItem.HasMember("bottomBackGroundImageFilePath"))
			{
				itemData->setBottomBackGroundImageFilePath(jsonItem["bottomBackGroundImageFilePath"].GetString());
			}
			if (jsonItem.HasMember("actionName"))
			{
				itemData->setAction(jsonItem["actionName"].GetString());
			}
			if(jsonItem.HasMember("packageName"))
			{
				itemData->setPackage(jsonItem["packageName"].GetString());
			}
			if(jsonItem.HasMember("label"))
			{
				itemData->setHintText(jsonItem["label"].GetString());
			}
			if(jsonItem.HasMember("className"))
			{
				itemData->setClass(jsonItem["className"].GetString());
			}
		}
		itemData->autorelease();
		return itemData;
	}
	CC_SAFE_DELETE(itemData);
	return NULL;
}

bool ItemData::init()
{
    return true;
}

void ItemData::setParmas(vector<Map<string, Ref*> > parmas)
{
    *m_parmas = parmas;
}

vector<Map<string, Ref*>>* ItemData::getParmas()
{
    return m_parmas;
}


