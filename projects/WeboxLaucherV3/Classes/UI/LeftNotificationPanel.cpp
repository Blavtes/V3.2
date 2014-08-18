/*
 * LeftNotificationPanel.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "LeftNotificationPanel.h"
#include "PrefixConst.h"
#include "NotificationItem.h"
#include "../Utils/HandleMessageQueue.h"

LeftNotificationPanel::LeftNotificationPanel()
{
	//.......
	m_statusFlag = false;
    m_imageLine = nullptr;
	m_titleImage = nullptr;
	m_titleText = nullptr;
	m_itemPanel = nullptr;
	m_focusHelper = nullptr;
	m_notificationCount = 0;
}

LeftNotificationPanel::~LeftNotificationPanel() {
	// ......
	m_focusHelper->release();
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
//	this->setBackGroundColor(Color3B::RED);
//	this->setBackGroundColorType(BackGroundColorType::SOLID);
	ui::ImageView* backgroundImage = ui::ImageView::create();
	backgroundImage->loadTexture(LEFT_NOTIFICATION_BACKGROUND_IMG);
	Size backgroundImageSize = backgroundImage->getContentSize();
	this->setContentSize(backgroundImageSize);
	backgroundImage->setPosition(Vec2(backgroundImageSize.width/2,backgroundImageSize.height/2));
	this->addChild(backgroundImage);

	m_titleImage = ui::ImageView::create();
	m_titleImage->loadTexture(MARK_NOTIFICATION_ICON_IMG);
	Size titleImageSize = m_titleImage->getContentSize();
	m_titleImage->setPosition(Vec2(60,this->getContentSize().height - titleImageSize.height/2-60));
	this->addChild(m_titleImage);

	m_titleText = ui::Text::create();
//	char leftNotificationTitle[20];
//	sprintf(leftNotificationTitle,LEFT_NEWNOTIFICATION_TITLE,m_notificationCount);
	m_titleText->setString(LEFT_NEWNOTIFICATION_TITLE);
	Size titleTextSize = m_titleText->getContentSize();
	m_titleText->setPosition(Vec2(140, m_titleImage->getPosition().y));
	m_titleText->setFontSize(35);
	m_titleText->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_titleText->setTextVerticalAlignment(TextVAlignment::CENTER);
	this->addChild(m_titleText);

	m_imageLine = ui::ImageView::create();
	m_imageLine->loadTexture(LEFT_LINE_IMAGE);
	Size imageLineSize = m_imageLine->getContentSize();
	m_imageLine->setPosition(Vec2(imageLineSize.width/2,m_titleImage->getPosition().y-30));
	this->addChild(m_imageLine);

	m_itemPanel = ItemPanel::create();
	m_itemPanel ->setItemPanelSize(Size(400,visibleSize.height-200));
	m_itemPanel->setMarginValue(10,10,10);
	m_itemPanel->setPosition(Vec2(20,m_imageLine->getPosition().y-m_itemPanel->getContentSize().height - 20));
	this->addChild(m_itemPanel,1);

	this->addTestItems();

	m_focusHelper = FocusHelper::create();
	m_focusHelper->bindItemPanel(m_itemPanel);
	m_focusHelper->retain();


	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();

	handleMessage->registerLayer(this,"NotificationApp");
	return true;
}

void LeftNotificationPanel::show()
{
	//
	if(!m_statusFlag)
	{
		m_statusFlag = true;
		MoveTo* move = MoveTo::create(0.3f,Vec2::ZERO);
		this->runAction(move);
	}
	else
	{
		MoveTo* move = MoveTo::create(0.3f,Vec2(-this->getContentSize().width,0));
		this->runAction(move);
		m_statusFlag = false;
	}

}

void LeftNotificationPanel::addTestItems()
{
	//
	NotificationItem* notificationItem1 = NotificationItem::create();
	ItemData* notificationItemData1 = ItemData::create();
	notificationItemData1 ->setBackgroundImageFilePath("image/notification/BG_up.png");
	notificationItemData1->setForegroundImageFilePath("image/notification/ic_update.png");
	notificationItemData1->setHintText("系统有更新");
	notificationItem1->setItemData(notificationItemData1);
	m_itemPanel->addItem(notificationItem1);

	NotificationItem* notificationItem2 = NotificationItem::create();
	ItemData* notificationItemData2 = ItemData::create();
	notificationItemData2 ->setBackgroundImageFilePath("image/notification/BG_up.png");
	notificationItemData2->setForegroundImageFilePath("image/notification/ic_like.png");
	notificationItemData2->setHintText("剧集有更新");
	notificationItem2->setItemData(notificationItemData2);
	m_itemPanel->addItem(notificationItem2);

	NotificationItem* notificationItem3 = NotificationItem::create();
	ItemData* notificationItemData3 = ItemData::create();
	notificationItemData3 ->setBackgroundImageFilePath("image/notification/BG_up.png");
	notificationItemData3->setForegroundImageFilePath("image/notification/ic_usb.png");
	notificationItemData3->setHintText("U盘已插入");
	notificationItem3->setItemData(notificationItemData3);
	m_itemPanel->addItem(notificationItem3);
}

void LeftNotificationPanel::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("LeftNotificationPanel-------the key:%d is pressed!---------------------------xjx",keyCode);
	if(m_focusHelper != NULL)
	{
		m_focusHelper->onKeyPressed(keyCode,event);
	}
}

void LeftNotificationPanel::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("LeftNotificationPanel-------the key:%d is released!---------------------------xjx",keyCode);
	if(m_focusHelper != NULL)
	{
		m_focusHelper->onKeyReleased(keyCode,event);
	}
}

bool LeftNotificationPanel::getLeftNotificationPanelStatus()
{
	return m_statusFlag;
}


void LeftNotificationPanel::receiveMessageData(std::string messageTitle, std::string jsonString)
{
	//
	m_titleText->setString(jsonString);
	log("LeftNotificationPanel,received data from thread is:%s-----------------------xjx",jsonString.c_str());
}
