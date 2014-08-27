/*
 * NotificationItem.cpp
 *
 *  Created on: 2014年7月30日
 *      Author: xjx
 */

#include "NotificationItem.h"
#include "PrefixConst.h"
USING_NS_CC;

NotificationItem::NotificationItem()
{
	m_itemSize = ITEM_SIZE_NOTIFICATION;
}

NotificationItem::~NotificationItem()
{
	// .......
}

bool NotificationItem::init()
{
	if(!BaseItem::init())
	{
		return false;
	}
	this->setContentSize(m_itemSize);

	return true;
}

void NotificationItem::setBackgroundImage(std::string backgroundImageFilePath)
{
	//
	if(!backgroundImageFilePath.empty())
	{
		m_backgroundImage-> loadTexture(backgroundImageFilePath);
		Size backgroundImageSize = m_backgroundImage->getContentSize();
		m_backgroundImage->setScale(m_itemSize.width/backgroundImageSize.width, m_itemSize.height/backgroundImageSize.height);
		m_backgroundImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/2));
	}
}

 void NotificationItem::setForegroundImage(std::string forgroundImageFilePath)
 {
	 //.....
	 if(!forgroundImageFilePath.empty())
	 {
		 m_foregroundImage->loadTexture(forgroundImageFilePath);
		 Size foregroundImageSize = m_foregroundImage->getContentSize();
		 m_foregroundImage->setPosition(Vec2(foregroundImageSize.width/2+10,m_itemSize.height/2));
	 }
 }

 void NotificationItem::setHintText(std::string hintText)
 {
	 //....
	 if(!hintText.empty())
	 {
		 m_hintText ->setString(hintText);
		 Size textSize = m_hintText->getContentSize();
		 m_hintText->setFontSize(28);
		 m_hintText->setPosition(Vec2(m_itemSize.width - textSize.width/2 -10,m_itemSize.height/2));
	 }
 }


 void NotificationItem::onEnterClicked(bool isLongPressed)
 {
 	log("Call The NotificationItem!------------------------xjx");
 	if(m_itemData->getCode() == 10 || m_itemData->getCode() == 12 )
 	{
 		JniUtil::startActivityJNI(m_itemData->getAction().c_str(),m_itemData->getPackage().c_str(),m_itemData->getClass().c_str());
 	}
 	else if(m_itemData->getCode() == 11 )
 	{
 		JniUtil::startActivityJNI(m_itemData->getAction().c_str(),nullptr,nullptr);
 	}

 }
