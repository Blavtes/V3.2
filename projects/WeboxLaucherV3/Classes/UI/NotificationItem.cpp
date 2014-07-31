/*
 * NotificationItem.cpp
 *
 *  Created on: 2014年7月30日
 *      Author: xjx
 */

#include "NotificationItem.h"
#include "PrefixConst.h"

NotificationItem::NotificationItem()
{
	m_notificationImage = NULL;
	m_notificationText = NULL;
}

NotificationItem::~NotificationItem()
{
	// .......
}

 NotificationItem* NotificationItem::create()
 {
	 NotificationItem* notificationItem = new NotificationItem();
	 if(notificationItem && notificationItem->init())
	 {
		 notificationItem->autorelease();
		 return notificationItem;
	 }
	 CC_SAFE_DELETE(notificationItem);
	 return NULL;
 }

bool NotificationItem::init()
{
	if(!Widget::init())
	{
		return false;
	}
	this->setSize(Size(220,60));

	ui::ImageView* backgroundImage = ui::ImageView::create();
	backgroundImage->loadTexture("image/notification/BG_up.png");
	backgroundImage->setScale(this->getSize().width/backgroundImage->getContentSize().width,this->getSize().height/backgroundImage->getContentSize().height);
	backgroundImage->setSize(this->getSize());
	backgroundImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/2));
	this->addChild(backgroundImage);

	m_notificationImage = ui::ImageView::create();
	m_notificationImage->setPosition(Vec2::ZERO);
	this->addChild(m_notificationImage);

	m_notificationText = ui::Text::create();
	m_notificationText->setPosition(Vec2::ZERO);
	m_notificationText->setFontSize(28);
	m_notificationText->setString("");
	this->addChild(m_notificationText);

	return true;
}

void NotificationItem::setNotificationImage(std::string notificationImageFilePath)
{
	//
	m_notificationImage->loadTexture(notificationImageFilePath);
	Size notificationImageSize = m_notificationImage->getContentSize();
	m_notificationImage->setPosition(Vec2(notificationImageSize.width/2+10,m_itemSize.height/2));
}

void NotificationItem::setNotificationText(std::string notificationText)
{
	//
	m_notificationText->setString(notificationText);
	Size notificationTextSize = m_notificationText->getContentSize();
	m_notificationText->setPosition(Vec2(m_itemSize.width - notificationTextSize.width/2 -10,m_itemSize.height/2));
}

Size NotificationItem::getSize()
{
	return m_itemSize;
}

void NotificationItem::setSize(Size itemSize)
{
	m_itemSize = itemSize;
}
