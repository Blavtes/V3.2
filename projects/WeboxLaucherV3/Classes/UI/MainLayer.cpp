/*
 * MainLayer.cpp
 *
 *  Created on: 2014年7月16日
 *      Author: xjx
 */

#include "MainLayer.h"
#include "UI/BaseItem.h"
#include "PrefixConst.h"


MainLayer::MainLayer()
{
	m_itemPanel = NULL;
	m_focusHelper = NULL;
	m_backgroundImageView = NULL;
	m_topBar = NULL;
	m_bottomBar = NULL;
	m_cibnText = NULL;
	m_notificationPanel = NULL;

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
    m_backgroundImageView->loadTexture("background.jpg");
    m_backgroundImageView->setPosition(Vec2(visibleSize.width/2,visibleSize.height/2));
    this->addChild(m_backgroundImageView,0);

    m_itemPanel=ItemPanel::create();
    m_itemPanel->setPosition(Vec2(10,visibleSize.height - 120 - ItemPanelSize.height-10));
    this->addChild(m_itemPanel,1);

    m_focusHelper = FocusHelper::create();
    m_focusHelper->bindItemPanel(m_itemPanel);
    m_focusHelper->retain();

    m_topBar = TopBarPanel::create();
   m_topBar->setPosition(Vec2(10,visibleSize.height-120));
    this->addChild(m_topBar,1);

    m_bottomBar = BottomBarPanel::create();
    m_bottomBar->setPosition(Vec2(10,10));
//    this->addChild(m_bottomBar,1);

    m_cibnText = ui::Text::create();
    m_cibnText->setPosition(Vec2(visibleSize.width/2,50));
    m_cibnText->setVisible(false);
    this->addChild(m_cibnText,1);

    m_notificationPanel = LeftNotificationPanel::create();
    Size notificationPanelSize = m_notificationPanel->getSize();
    m_notificationPanel->setPosition(Vec2(-notificationPanelSize.width,0));
    this->addChild(m_notificationPanel,2);

	auto listener = EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(MainLayer::onKeyPressed,this);
	listener->onKeyReleased= CC_CALLBACK_2(MainLayer::onKeyReleased,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener,this);

	return true;
}

void MainLayer::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("the key:%d is pressed!---------------------------xjx",keyCode);
	if(m_focusHelper != NULL)
	{
		m_focusHelper->onKeyPressed(keyCode,event);
	}
}

void MainLayer::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
	log("the key:%d is released!---------------------------xjx",keyCode);
	if(m_focusHelper != NULL)
	{
		m_focusHelper->onKeyReleased(keyCode,event);
	}
	if(keyCode == EventKeyboard::KeyCode::KEY_MENU )
	{
		m_notificationPanel ->show();
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




