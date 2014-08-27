/*
 * MainLayer.cpp
 *
 *  Created on: 2014年7月16日
 *      Author: xjx
 */
#include "PrefixConst.h"
#include "MainLayer.h"
#include "BaseItem.h"
#include "MainItem.h"
#include "AppItem.h"
#include "Utils/ParseJson.h"
#include "Utils/HandleMessageQueue.h"
#include "Utils/JniUtil.h"
#include "ToastTextView.h"
#include "json/rapidjson.h"
#include "json/document.h"



MainLayer::MainLayer()
{
	m_itemPanel = nullptr;
	m_focusHelper = nullptr;
	m_backgroundImageView = nullptr;
	m_topBar = nullptr;
	m_notificationPanel = nullptr;
	m_airPlayPanel = nullptr;
}

MainLayer::~MainLayer()
{
	m_focusHelper->release();
}

Scene* MainLayer::createScene()
{
	auto scene=Scene::create();
	auto layer=MainLayer::create();
	scene->addChild(layer);
    return scene;
}


bool MainLayer::init()
{
	if(!Layer::init()){
		return false;
	}

	Size visibleSize=Director::getInstance()->getVisibleSize();
    m_backgroundImageView =ui:: ImageView::create(MAIN_LAYER_BACKGROUND_IMG);
    m_backgroundImageView->setPosition(Vec2(visibleSize.width/2,visibleSize.height/2));
    this->addChild(m_backgroundImageView,0);

    m_itemPanel=ItemPanel::create();
	m_itemPanel->setItemPanelSize(ITEM_PANEL_SIZE);
	m_itemPanel->setPosition(Vec2::ZERO);
    this->addChild(m_itemPanel,1);

    m_itemPanel->addDefaultMainItemByPlistFile("plist/mainData.plist");
    log("Add the local Resource Completely!-------------------------@xjx------");

    m_focusHelper = FocusHelper::create();
    m_focusHelper->bindItemPanel(m_itemPanel);  //delay bind
    m_focusHelper->retain();

    m_topBar = TopBarPanel::create();
	m_topBar->setPosition(Vec2(10,visibleSize.height-120));
    this->addChild(m_topBar,1);

    m_notificationPanel = LeftNotificationPanel::create();
    Size notificationPanelSize = m_notificationPanel->getContentSize();
    m_notificationPanel->setPosition(Vec2(-notificationPanelSize.width,0));
    this->addChild(m_notificationPanel,20);

    m_airPlayPanel = AirPlayPanel::create();
    m_airPlayPanel->setPosition(Vec2::ZERO);
    this->addChild(m_airPlayPanel);

	auto listener = EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(MainLayer::onKeyPressed,this);
	listener->onKeyReleased= CC_CALLBACK_2(MainLayer::onKeyReleased,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener,this);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(MainLayer::updateCIBNAuthorization,this),"Cibn");

	this->scheduleUpdate();
	return true;
}


void MainLayer::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("@xjx--MainLayer----he key:%d is pressed!---------------------------xjx",keyCode);
	if(m_focusHelper != nullptr && !m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_focusHelper->onKeyPressed(keyCode,event);
	}
	if(m_notificationPanel != nullptr  && m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_notificationPanel->onKeyPressed(keyCode,event);
	}
}

void MainLayer::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("MainLayer----the key:%d is released!---------------------------xjx",keyCode);
	if(m_focusHelper != nullptr &&  !m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_focusHelper->onKeyReleased(keyCode,event);
	}

	if(m_notificationPanel != nullptr  && m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_notificationPanel->onKeyReleased(keyCode,event);
	}

	if(keyCode == EventKeyboard::KeyCode::KEY_MENU )
	{
		log("MainLayer----the key Menu is clicked!---------------------------xjx");
		m_notificationPanel ->show();
	}

}

void MainLayer::update(float dt)
{
	//
	m_topBar->updateTimeState();
	int messageCount = m_notificationPanel->getNotificationCount();
	m_topBar->updateNotificationMessageCountState(messageCount);
	m_airPlayPanel->moveIconPosition(messageCount != 0);
	if(m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_focusHelper->clearFocusIndicator();
	}
	if(m_focusHelper ->getSelectedItemIndex() == 0 && m_itemPanel->getAllItems().size() > 0 )
	{
		m_focusHelper->initFocusIndicator();
	}
	if(m_focusHelper->getSelectedItemIndex() > 0 && !m_notificationPanel->getLeftNotificationPanelStatus())
	{
		m_focusHelper->showFocusIndicator();
	}
}

void MainLayer::updateCIBNAuthorization(std::string jsonString)
{
	Size visibleSize=Director::getInstance()->getVisibleSize();
	ValueMap resultData = ParseJson::getInfoDataFromJSON(jsonString);
	int code = resultData["arg0"].asInt();
	int result = resultData["arg1"].asInt();
	if(code == 0xffffffff)
	{
		return;
	}
	else if(code == 0)
	{
		ToastTextView::getInstance(this)->showMsg(CIBN_AUTH_BEGIN_TXT,3,this,Vec2(visibleSize.width/2,50));
	}
	else if(result == 1)
	{
		ToastTextView::getInstance(this)->showMsg(CIBN_AUTH_END_SUCC_TXT,3,this,Vec2(visibleSize.width/2,50));
	}
	else
	{
		ToastTextView::getInstance(this)->showMsg(CIBN_AUTH_END_FAILE_TXT,3,this,Vec2(visibleSize.width/2,50));
	}

}

void MainLayer::addTestItems()
{
    auto item4=AppItem::create();
    auto itemData4 = ItemData::create();
    itemData4->setBackgroundImageFilePath(APPITEM_SET_BG_IMG);
    itemData4->setForegroundImageFilePath(APPITEM_SET_FG_IMG);
    itemData4->setHintText("app 1");
    itemData4->setPackage("");
    item4->setItemData(itemData4);
    m_itemPanel->addItem(item4);

    auto item5=AppItem::create();
    auto itemData5 = ItemData::create();
    itemData5->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
    itemData5->setForegroundImageFilePath(APPITEM_FILE_FG_IMG);
    itemData5->setHintText("app 2");
    itemData5->setPackage("");
    item5->setItemData(itemData5);
    m_itemPanel->addItem(item5);
}







