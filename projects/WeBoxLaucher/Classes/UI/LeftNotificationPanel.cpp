/*
 * LeftNotificationPanel.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "LeftNotificationPanel.h"
#include "PrefixConst.h"
#include "NotificationItem.h"

LeftNotificationPanel::LeftNotificationPanel()
{
	//.......
	m_showFlag = false;
    m_imageLine = NULL;
	m_titleImage = NULL;
	 m_titleText = NULL;
	 m_itemPanel = NULL;

}

LeftNotificationPanel::~LeftNotificationPanel() {
	// ......
}

LeftNotificationPanel* LeftNotificationPanel::create()
{
	LeftNotificationPanel* notificationPanel = new LeftNotificationPanel();
	if(notificationPanel && notificationPanel->init())
	{
		notificationPanel->autorelease();
		return notificationPanel;
	}
	CC_SAFE_DELETE(notificationPanel);
	return NULL;
}

bool LeftNotificationPanel::init()
{
	if(!Layout::init())
	{
		return false;
	}
	Size visibleSize = Director::getInstance()->getVisibleSize();
//	this->setBackGroundColor(Color3B::YELLOW);
//	this->setBackGroundColorType(BackGroundColorType::SOLID);
	ui::ImageView* backgroundImage = ui::ImageView::create();
	backgroundImage->loadTexture(Left_Notificaiton_Background_Image);
	Size backgroundImageSize = backgroundImage->getSize();
	this->setSize(backgroundImageSize);
	backgroundImage->setPosition(Vec2(backgroundImageSize.width/2,backgroundImageSize.height/2));
	this->addChild(backgroundImage);

	m_titleImage = ui::ImageView::create();
	m_titleImage->loadTexture(MarkNotification_Icon_Image);
	Size titleImageSize = m_titleImage->getContentSize();
	m_titleImage->setPosition(Vec2(60,this->getSize().height - titleImageSize.height/2-60));
	this->addChild(m_titleImage);

	m_titleText = ui::Text::create();
	m_titleText->setString(Left_NewNotification_Title);
	Size titleTextSize = m_titleText->getContentSize();
	m_titleText->setPosition(Vec2(140, m_titleImage->getPosition().y));
	m_titleText->setFontName("");
	m_titleText->setFontSize(35);
	m_titleText->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_titleText->setTextVerticalAlignment(TextVAlignment::CENTER);
	this->addChild(m_titleText);

	m_imageLine = ui::ImageView::create();
	m_imageLine->loadTexture(LEFT_LINE_IMAGE);
	Size imageLineSize = m_imageLine->getSize();
	m_imageLine->setPosition(Vec2(m_imageLine->getSize().width/2,m_titleImage->getPosition().y-30));
	this->addChild(m_imageLine);

	m_itemPanel = ui::ScrollView::create();
	m_itemPanel ->setSize(Size(400,visibleSize.height-200));
	m_itemPanel->setPosition(Vec2(10,m_imageLine->getPosition().y-m_itemPanel->getSize().height - 20));
	this->addChild(m_itemPanel,1);

	//...the code for test
	NotificationItem* notificationItem = NotificationItem::create();
	notificationItem->setNotificationImage("image/notification/ic_update.png");
	notificationItem->setNotificationText("系统有更新");
	Size notificationItemSize = notificationItem->getSize();
	notificationItem->setPosition(Vec2(10,m_itemPanel->getSize().height-20-notificationItemSize.height/2));
	m_itemPanel->addChild(notificationItem,2);



	return true;
}

void LeftNotificationPanel::show()
{
	//
	if(!m_showFlag)
	{
		m_showFlag = true;
		MoveTo* move = MoveTo::create(0.3f,Vec2::ZERO);
		this->runAction(move);
	}
	else
	{
		MoveTo* move = MoveTo::create(0.3f,Vec2(-this->getSize().width,0));
		this->runAction(move);
		m_showFlag = false;
	}

}

