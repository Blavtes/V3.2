/*
 * TopBarLayer.cpp
 *
 *  Created on: 2014年7月25日
 *      Author: xjx
 */

#include "TopBarPanel.h"
#include "PrefixConst.h"

TopBarPanel::TopBarPanel() {
    m_timeText = NULL;
    m_dateText = NULL;
    m_weekdayText = NULL;
    m_networkImageView = NULL;
    m_cibnImageView = NULL;

}

TopBarPanel::~TopBarPanel() {
	//.......
}

TopBarPanel* TopBarPanel::create()
{
	TopBarPanel* topBarPanel= new TopBarPanel();
	if(topBarPanel && topBarPanel->init())
	{
		topBarPanel->autorelease();
		return topBarPanel;
	}
	CC_SAFE_DELETE(topBarPanel);
	return NULL;
}

bool TopBarPanel::init()
{
	//
	if(!Layout::init())
	{
		return false;
	}
	Size visibleSize = Director::getInstance()->getVisibleSize();
	this->setSize(Size(visibleSize.width-20,100));
//	this->setBackGroundColor(Color3B::RED);
//	this->setBackGroundColorType(BackGroundColorType::SOLID);


	m_timeText = ui::Text::create();
	m_timeText->setPosition(Vec2(visibleSize.width-250, this->getSize().height/2));
	m_timeText->setSize(Size(100, 50));
//	m_timeText->enableShadow(ccp(0, -3), 255*0.3, 3,ccBLACK,true);
	m_timeText->setColor(Color3B(174, 174, 174));
	m_timeText->setFontSize(40);
	m_timeText->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_timeText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_timeText);

    m_dateText = ui::Text::create();
    m_dateText->setPosition(Vec2(m_timeText->getPosition().x+90, m_timeText->getPosition().y+10));
    m_dateText->setSize(Size(100, 50));
    m_dateText->setColor(Color3B(174, 174, 174));
    m_dateText->setFontSize(15);
    m_dateText->setTextHorizontalAlignment( TextHAlignment::CENTER);
    m_dateText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_dateText);

    m_weekdayText = ui::Text::create();
    m_weekdayText->setPosition(Vec2(m_dateText->getPosition().x,m_dateText->getPosition().y-20));
    m_weekdayText->setSize(Size(100, 50));
    m_weekdayText->setColor(Color3B(174, 174, 174));
    m_weekdayText->setFontSize(15);
    m_weekdayText->setTextHorizontalAlignment( TextHAlignment::CENTER);
    m_weekdayText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_weekdayText);

   m_networkImageView = ui::ImageView::create();
   m_networkImageView->loadTexture("image/wifi/network_eth.png");
    m_networkImageView->setPosition(Vec2(m_dateText->getPosition().x + 64,this->getSize().height/2));
    m_networkImageView->setContentSize(Size(36, 31));
    this->addChild(m_networkImageView);

    ui::ImageView* imageLine =  ui::ImageView::create();
    imageLine->loadTexture("image/wifi/wife_line.png");
    imageLine->setPosition(Vec2(m_dateText->getPosition().x + 38,this->getSize().height/2));
    this->addChild(imageLine);
    imageLine->setScaleY(0.94f);

    m_cibnImageView = ui::ImageView::create();
    m_cibnImageView->loadTexture("image/other/ic_mainmenu_CIBN.png");
    m_cibnImageView->setPosition(Vec2(150,this->getSize().height/2-20));
    this->addChild(m_cibnImageView);

    this->scheduleUpdate();
	return true;
}


void TopBarPanel::updateWifiState(ValueMap& wifiStateMap)
{
   int state=  wifiStateMap.at("state").asInt();
   int level =wifiStateMap.at("level").asInt();
   char  wifiImageFilePath[100];
   sprintf(wifiImageFilePath,NetWork_image_Wifi,level);
   switch(state)
   {
   case kNetwork_State_Disable:
	   m_networkImageView->loadTexture(NetWork_image_disable);
    	break;
   case kNetwork_State_Ethernet:
    	m_networkImageView->loadTexture(NetWork_image_net);
    	break;
   case kNetwork_State_Wifi:
    	m_networkImageView->loadTexture(wifiImageFilePath);
    	break;
   default:
    	break;
    }
}

void TopBarPanel::update(float dt)
{
    time_t now;
    time(&now);
    struct tm *ptm = localtime(&now);
    int year = ptm->tm_year + 1900;
    int month = ptm->tm_mon + 1;
    int day = ptm->tm_mday;
    int hour = ptm->tm_hour;
    int min = ptm ->tm_min;
    int weekday = ptm->tm_wday;

   char dateString[10];
   sprintf(dateString,"%d月%d日", month, day);
   m_dateText->setString(dateString);

   char* weekdayString;
   switch(weekday)
   {
   case 0:
	   weekdayString = "星期日";
	   break;
   case 1:
	   weekdayString = "星期一";
	   break;
   case 2:
	   weekdayString = "星期二";
	   break;
   case 3:
	   weekdayString = "星期三";
	   break;
   case 4:
	   weekdayString = "星期四";
	   break;
   case 5:
	   weekdayString = "星期五";
	   break;
   case 6:
	   weekdayString = "星期六";
	   break;
   default:
	   break;
   }
   m_weekdayText ->setString(weekdayString);

   char timeString[10];
   if(min < 10)
   {
	   sprintf(timeString,"%d:0%d", hour, min);
   }
   else
   {
	   sprintf(timeString,"%d:%d", hour, min);
   }
   m_timeText ->setString(timeString);
}
