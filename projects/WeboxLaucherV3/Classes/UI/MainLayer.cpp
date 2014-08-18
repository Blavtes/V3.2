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
#include "Data/ParseJson.h"
#include "../Utils/HandleMessageQueue.h"
#include "../Utils/JniUtil.h"



MainLayer::MainLayer()
{
	m_itemPanel = nullptr;
	m_focusHelper = nullptr;
	m_backgroundImageView = nullptr;
	m_topBar = nullptr;
	m_cibnText = nullptr;
	m_notificationPanel = nullptr;
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
	log("axxxx------------mainlayer begin,begin to add the bacround Image!");
	if(!Layer::init()){
		return false;
	}

	Size visibleSize=Director::getInstance()->getVisibleSize();
    m_backgroundImageView =ui:: ImageView::create(MAIN_LAYER_BACKGROUND_IMG);
    m_backgroundImageView->setPosition(Vec2(visibleSize.width/2,visibleSize.height/2));
    this->addChild(m_backgroundImageView,0);
	log("axxxx------------background Image add complete! begin to create ItemPanel!");
    m_itemPanel=ItemPanel::create();
	m_itemPanel->setItemPanelSize(ITEM_PANEL_SIZE);
	m_itemPanel->setPosition(Vec2::ZERO);
    this->addChild(m_itemPanel,1);


//    m_itemPanel->addDefaultMainItemByPlistFile("plist/mainData.plist");
//    m_itemPanel->addDefaultAppItem();
    //this->addTestItems();

//    m_itemPanel->addDefaultMainItemByPlistFile("plist/mainData.plist");
//    m_itemPanel->addDefaultAppItem();
    //this->addTestItems();

	log("axxxx------------Default AppItem add Compelete! begin to add TopBarPanel!");
    m_focusHelper = FocusHelper::create();
//    m_focusHelper->bindItemPanel(m_itemPanel);  //delay bind
    m_focusHelper->retain();



    m_topBar = TopBarPanel::create();
	m_topBar->setPosition(Vec2(10,visibleSize.height-120));
    this->addChild(m_topBar,1);


    m_cibnText = ui::Text::create();
    m_cibnText->setPosition(Vec2(visibleSize.width/2,50));
    m_cibnText->setString("cibn 验证");
    m_cibnText->setVisible(true);
    m_cibnText->setFontSize(28);
    this->addChild(m_cibnText,1);
	log("axxxx------------TopBarPanel added complete! begin to add LeftNotificationPanel!");

    m_notificationPanel = LeftNotificationPanel::create();
    Size notificationPanelSize = m_notificationPanel->getContentSize();
    m_notificationPanel->setPosition(Vec2(-notificationPanelSize.width,0));
    this->addChild(m_notificationPanel,20);
	log("axxxx------------LeftNotificationPanel add Complete!");

	auto listener = EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(MainLayer::onKeyPressed,this);
	listener->onKeyReleased= CC_CALLBACK_2(MainLayer::onKeyReleased,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener,this);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();

	handleMessage->registerLayer(this,"MainApp");
	handleMessage->registerLayer(this,"UserApp");

//	this->scheduleOnce(schedule_selector(MainLayer::insertItem),3);
	return true;
}


void MainLayer::insertItem(float dt)
{
	//
    auto item4=AppItem::create();
    auto itemData4 = ItemData::create();
    itemData4->setBackgroundImageFilePath(APPITEM_SET_BG_IMG);
    itemData4->setForegroundImageFilePath(APPITEM_SET_FG_IMG);
    itemData4->setHintText("app 1");
    itemData4->setPackage("");
    item4->setItemData(itemData4);
    m_itemPanel->insertItemByIndex(item4,3);

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
	if(keyCode == EventKeyboard::KeyCode::KEY_MENU )
	{
		log("MainLayer----the key Menu is clicked!---------------------------xjx");
		m_notificationPanel ->show();
		if(m_notificationPanel->getLeftNotificationPanelStatus())
		{
			m_focusHelper->clearFocusIndicator();
		}
		else
		{
			m_focusHelper->showFocusIndicator();
		}
	}
}

void MainLayer::CIBNAuthorization(ValueMap& map)
{
    int code =map.at(CIBN_CODE_KEY).asInt();
    int result = map.at(CIBN_RESULT_KEY).asInt();
    m_cibnText->setVisible(true);
    if(code == 0)
    {
    	 m_cibnText->setString(CIBN_AUTH_BEGIN_TXT);
    }
    else
    {
    	if(result == 1)
    	{
    		m_cibnText->setString(CIBN_AUTH_END_SUCC_TXT);
    		FadeTo *fade = FadeTo::create(3.0f,235);
    		m_cibnText->runAction(fade);
    	}
    	else
    	{
    		m_cibnText->setString(CIBN_AUTH_END_FAILE_TXT);
    		FadeTo *fade = FadeTo::create(3.0f,235);
    		m_cibnText->runAction(fade);
    	}
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

void MainLayer::receiveMessageData(std::string messageTitle,std::string jsonString)
{
	//
	log("MainLayer:prepared to process message!--------------------@xjx");
	log("MainLayer:json is:%s-----------------------@xjx",jsonString.c_str());
	m_focusHelper->clearFocusIndicator();

	Vector<ItemData*> itemVector;
	if(!ParseJson::getItemVectorFromJSON(jsonString, itemVector))
	{
		log("MainLayer:parse json Failed~~~~~~~~~~~~~~~~~~~~~~~~~~@xjx");
		return;
	}
	if(messageTitle.compare("MainApp") == 0)
	{
		log("Update MainApp--------------------------------------------------@xjx");
		m_itemPanel->updateMainApps(itemVector);
	}
	else if(messageTitle.compare("UserApp") == 0)
	{
		log("Update UserApp--------------------------------------------------@xjx");
		m_itemPanel->updateUserApps(itemVector);
	}
//	m_itemPanel->updateAllItems(itemVector);
	if(m_focusHelper ->getSelectedItemIndex() == 0)
	{
		m_focusHelper->initFocusIndicator();

	}
	m_focusHelper->showFocusIndicator();

}












