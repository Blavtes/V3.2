/*
 * ItemData.cpp
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#include "ItemData.h"

ItemData::ItemData() {
	// TODO Auto-generated constructor stub
	m_backgroundImageFilePath=NULL;
	m_foregroundImageFilePath = NULL;
	m_hintText = NULL;
	m_bottomBackGroundImageFilePath= NULL;
	m_isClip = false;
	m_id = 0;
}

ItemData::~ItemData() {
	// TODO Auto-generated destructor stub
}

ItemData* ItemData::create()
{
	ItemData* itemData = new ItemData();
	if(itemData && itemData->init()){
		CC_SAFE_RETAIN(itemData);
		return itemData;
	}
	CC_SAFE_DELETE(itemData);
	return NULL;
}

 bool ItemData::init()
{
	return true;
}

void ItemData::setBackgroundImageFilePath(const char* backgroundImageFilePath)
{
	this->m_backgroundImageFilePath = backgroundImageFilePath;
}
const char* ItemData::getBackgroundImageFilePath()
{
	return m_backgroundImageFilePath;
}
