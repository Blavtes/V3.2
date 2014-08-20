/*
 * LeftNotificationPanel.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "LeftNotificationPanel.h"
#include "PrefixConst.h"
#include "NotificationItem.h"
#include "Utils/HandleMessageQueue.h"
#include "Utils/ParseJson.h"

LeftNotificationPanel::LeftNotificationPanel()
{
	//.......
	m_statusFlag = false;
    m_imageLine = nullptr;
	m_titleImage = nullptr;
	m_titleText = nullptr;
	m_itemPanel = nullptr;
	m_focusHelper = nullptr;
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

	m_focusHelper = FocusHelper::create();
	m_focusHelper->bindItemPanel(m_itemPanel);
	m_focusHelper->retain();

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(LeftNotificationPanel::updateLeftNotification,this),"NotificationApp");
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

void LeftNotificationPanel::updateLeftNotification(std::string jsonString)
{
	m_focusHelper->clearFocusIndicator();

	ValueMap resultData = ParseJson::getInfoDataFromJSON(jsonString);
	int code = resultData["arg0"].asInt();
	bool state = resultData["arg1"].asBool();
	std::string message = resultData["arg2"].asString();
	log("The received message is code : %d, message : %s=================@NotificationApp",code,message.c_str());
	if(code == 0xffffffff)
	{
		return;
	}

	for(int i = 0; i < m_itemPanel->getAllItems().size(); i++)
	{
		BaseItem* tempItem = m_itemPanel->getAllItems().at(i);
		if(tempItem->getItemData()->getCode() == code && state)
		{
			m_focusHelper->showFocusIndicator();
			return;
		}
		else if(tempItem->getItemData()->getCode() == code)
		{
			m_itemPanel->removeItemByIndex(i);
			m_focusHelper->showFocusIndicator(); //---------after deleted Item, should be re-show the indicator
			return ;
		}
	}

	ItemData* notificationItemData = ItemData::create();
	notificationItemData ->setBackgroundImageFilePath(BANNER_Background_Image);
	if(code == CODE_SYSTEM_UPGRADE)
	{
		notificationItemData->setCode(code);
		notificationItemData->setForegroundImageFilePath(Left_UpdateIcon_Image);
		notificationItemData->setHintText(Left_Item_Update_Title);
	}
	else if(code == CODE_CHASE_DRAMA)
	{
		notificationItemData->setCode(code);
		notificationItemData->setForegroundImageFilePath(Left_LikeIcon_Image);
		notificationItemData->setHintText(Left_Item_Like_Title);
	}
	else if(code == CODE_MOUNT_UNMOUNT)
	{
		notificationItemData->setCode(code);
		notificationItemData->setForegroundImageFilePath(Left_USBIcon_Image);
		notificationItemData->setHintText(Left_Item_USB_Insert_Title);
	}

	NotificationItem* notificationItem = NotificationItem::create();
	notificationItem->setItemData(notificationItemData);
	m_itemPanel->addItem(notificationItem);

	if(m_focusHelper ->getSelectedItemIndex() == 0 && m_itemPanel->getAllItems().size() > 0)
	{
		m_focusHelper->initFocusIndicator();
	}
	if(m_focusHelper->getSelectedItemIndex() > 0)
	{
		m_focusHelper->showFocusIndicator();
	}
}

int LeftNotificationPanel::getNotificationCount()
{
	return m_itemPanel->getAllItems().size();
}
