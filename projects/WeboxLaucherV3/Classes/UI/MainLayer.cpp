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
#include "ClearnAppItem.h"
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

	m_focusHelper = FocusHelper::create();
	m_focusHelper->bindItemPanel(m_itemPanel);  //delay bind
	m_focusHelper->retain();

//	this->addTestItems(0);

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

	auto listenerKeyboard = EventListenerKeyboard::create();
	listenerKeyboard->onKeyPressed = CC_CALLBACK_2(MainLayer::onKeyPressed,this);
	listenerKeyboard->onKeyReleased= CC_CALLBACK_2(MainLayer::onKeyReleased,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listenerKeyboard,this);

	this->setTouchMode(Touch::DispatchMode::ONE_BY_ONE);
	auto listenerTouch = EventListenerTouchOneByOne::create();
	listenerTouch->onTouchBegan = CC_CALLBACK_2(MainLayer::onTouchBegan,this);
	listenerTouch->onTouchEnded = CC_CALLBACK_2(MainLayer::onTouchEnded,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listenerTouch,this);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(MainLayer::updateCIBNAuthorization,this),"Cibn");

	this->scheduleUpdate();
	this->scheduleOnce(schedule_selector(MainLayer::addTestItems),3);
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


bool MainLayer::onTouchBegan(Touch *touch, Event *unusedEvent)
{
	 log("@touch-------------------------MainLayer::Call the onTouchBegan!");
//	 if(!m_notificationPanel->getLeftNotificationPanelStatus())
//	 {
//		 //
//		 m_itemPanel->setTouchEnabled(false);
//		 return false;
//	 }
//	 else
//	 {
//		 m_itemPanel->setTouchEnabled(true);
//	 }
	 return true;
}

 void MainLayer::onTouchMoved(Touch *touch, Event *unused_event)
{
	return;
}
 void MainLayer::onTouchCancelled(Touch *touch, Event *unused_event)
{
	return;
}

 void MainLayer::onTouchEnded(Touch *touch, Event *unusedEvent)
{
	  Vec2 startPos = touch->getStartLocation();
	  Vec2 endPos = touch->getLocation();
	  Vec2 deltaPos = endPos - startPos;
	  if(!m_notificationPanel->getLeftNotificationPanelStatus())
	  {
		  for(BaseItem* item : m_itemPanel->getAllItems())
		  {
			  if(item->hitTest(startPos) && deltaPos.length() < 10 )
			  {
				  log("@touch-------------------------MainLayer::Call the onTouchEnded !!!!!");
				  item->onEnterClicked(false);
				  int focusIndex = m_itemPanel->getAllItems().getIndex(item);
				  if(focusIndex > 0  && focusIndex <= m_itemPanel->getAllItems().size())
				  {
					  m_focusHelper->clearFocusIndicator();
					  m_focusHelper->setSelectedItemIndex(focusIndex+1);
					  m_focusHelper->showFocusIndicator();
				  }
			  }
		  }
	  }
	  return;
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
	if(m_focusHelper ->getSelectedItemIndex() == 0 && m_itemPanel->getMainItemCount() > 0 && m_itemPanel->getAllItems().size() > 0 )
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

void MainLayer::addTestItems(float dt)
{
	ItemData* clearData = ItemData::create();
	clearData->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
	clearData->setHintText(APP_CLEARN_TITLE);
	clearData->setPackage("");
	clearData->setClass("");
	ClearnAppItem* clearnItem = ClearnAppItem::create();
	clearnItem->setItemData(clearData);
//	m_itemPanel->addItem(clearnItem);
	if(m_itemPanel->getAllItems().size() > m_itemPanel->getMainItemCount()+3)
	{
		m_itemPanel->insertItemByIndex(clearnItem,m_itemPanel->getMainItemCount()+3);
	}
}







