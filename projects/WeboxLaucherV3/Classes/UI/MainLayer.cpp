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
	if(!Layer::init()){
		return false;
	}

	Size visibleSize=CCDirector::getInstance()->getVisibleSize();
	log("The visible size is width:%f, height:%f.---------xjx", visibleSize.width, visibleSize.height);
    m_backgroundImageView = ui::ImageView::create();
    m_backgroundImageView->loadTexture(MAIN_LAYER_BACKGROUND_IMG);
    m_backgroundImageView->setPosition(Vec2(visibleSize.width/2,visibleSize.height/2));
    this->addChild(m_backgroundImageView,0);

    m_itemPanel=ItemPanel::create();
	m_itemPanel->setItemPanelSize(ITEM_PANEL_SIZE);
	m_itemPanel->setPosition(Vec2::ZERO);
    this->addChild(m_itemPanel,1);
    m_itemPanel->addDefaultMainItemByPlistFile("plist/mainData.plist");
    m_itemPanel->addDefaultAppItem();
   this->addTestItems();

    m_focusHelper = FocusHelper::create();
    m_focusHelper->bindItemPanel(m_itemPanel);
    m_focusHelper->retain();

    m_topBar = TopBarPanel::create();
	m_topBar->setPosition(Vec2(10,visibleSize.height-120));
    this->addChild(m_topBar,1);


    m_cibnText = ui::Text::create();
    m_cibnText->setPosition(Vec2(visibleSize.width/2,50));
    m_cibnText->setVisible(false);
    this->addChild(m_cibnText,1);

    m_notificationPanel = LeftNotificationPanel::create();
    Size notificationPanelSize = m_notificationPanel->getSize();
    m_notificationPanel->setPosition(Vec2(-notificationPanelSize.width,0));
    this->addChild(m_notificationPanel,20);

	auto listener = EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(MainLayer::onKeyPressed,this);
	listener->onKeyReleased= CC_CALLBACK_2(MainLayer::onKeyReleased,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener,this);

	return true;
}



void MainLayer::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("MainLayer----he key:%d is pressed!---------------------------xjx",keyCode);
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

    auto item6=AppItem::create();
    auto itemData6 = ItemData::create();
    itemData6->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
    itemData6->setForegroundImageFilePath(APPITEM_SET_FG_IMG);
    itemData6->setHintText("app 3");
    itemData6->setPackage("");
    item6->setItemData(itemData6);
    m_itemPanel->addItem(item6);

    auto item7=AppItem::create();
    auto itemData7 = ItemData::create();
    itemData7->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
    itemData7->setForegroundImageFilePath(APPITEM_FILE_FG_IMG);
    itemData7->setHintText("app 4");
    itemData7->setPackage("");
    item7->setItemData(itemData7);
    m_itemPanel->addItem(item7);

    auto item8=AppItem::create();
    auto itemData8 = ItemData::create();
    itemData8->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
    itemData8->setForegroundImageFilePath(APPITEM_FILE_FG_IMG);
    itemData8->setHintText("app 5");
    itemData8->setPackage("");
    item8->setItemData(itemData8);
    m_itemPanel->addItem(item8);

    auto parseJsonUtil = ParseJson::create("json/webox_launcher_metro.json");
    Vector<ItemData*> itemVector = parseJsonUtil->getItemDataVectot();
    log("The item count geted from Json File is:%d---------------------------xjx",itemVector.size());
//    auto item = MainItem::create();
//    auto itemData = itemVector.at(0);
//    log("the background image file path:%s----------------xjx", itemData->getBackgroundImageFilePath().c_str());
//    log("the fore ground image file path:%s-------------xjx", itemData->getForegroundImageFilePath().c_str());
//    log("the hint text is %s-----xjx",itemData->getHintText().c_str());
//    log("the bottom background image file path:%s-------xjx",itemData->getBottomBackGroundImageFilePath().c_str());
//    item->setItemData(itemData);
//    m_itemPanel->addItem(item);

    for(auto itemData : itemVector)
    {
    	auto item = MainItem::create();
    	item->setItemData(itemData);
    	m_itemPanel->addItem(item);
    }


}




