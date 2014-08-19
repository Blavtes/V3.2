/*
 * TopBarLayer.cpp
 *
 *  Created on: 2014年7月25日
 *      Author: xjx
 */

#include "TopBarPanel.h"
#include "PrefixConst.h"
#include "ToastTextView.h"
#include "Data/ParseJson.h"

TopBarPanel::TopBarPanel() {
    m_timeText = nullptr;
    m_dateText = nullptr;
    m_weekdayText = nullptr;
    m_networkImageView = nullptr;
    m_cibnImageView = nullptr;
    m_notificationHintImage = nullptr;
    m_notificationCountImage = nullptr;

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
	m_timeText->setPosition(Vec2(visibleSize.width-250, this->getContentSize().height/2));
	m_timeText->setSize(Size(100, 50));
	m_timeText->enableShadow(Color4B::BLACK,Size(0, -3),3);
	m_timeText->setColor(Color3B(174, 174, 174));
	m_timeText->setFontSize(40);
	m_timeText->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_timeText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_timeText);

    m_dateText = ui::Text::create();
    m_dateText->setPosition(Vec2(m_timeText->getPosition().x+90, m_timeText->getPosition().y+10));
    m_dateText->setSize(Size(100, 50));
    m_dateText->enableShadow(Color4B::BLACK,Size(0, -3),3);
    m_dateText->setColor(Color3B(174, 174, 174));
    m_dateText->setFontSize(15);
    m_dateText->setTextHorizontalAlignment( TextHAlignment::CENTER);
    m_dateText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_dateText);

    m_weekdayText = ui::Text::create();
    m_weekdayText->setPosition(Vec2(m_dateText->getPosition().x,m_dateText->getPosition().y-20));
    m_weekdayText->setSize(Size(100, 50));
    m_weekdayText->enableShadow(Color4B::BLACK,Size(0, -3),3);
    m_weekdayText->setColor(Color3B(174, 174, 174));
    m_weekdayText->setFontSize(15);
    m_weekdayText->setTextHorizontalAlignment( TextHAlignment::CENTER);
    m_weekdayText->setTextVerticalAlignment(TextVAlignment::CENTER);
    this->addChild(m_weekdayText);

   m_networkImageView = ui::ImageView::create();
   m_networkImageView->loadTexture(NETWORK_DISABLE_IMG);
    m_networkImageView->setPosition(Vec2(m_dateText->getPosition().x + 64,this->getContentSize().height/2));
    m_networkImageView->setContentSize(Size(36, 31));
    this->addChild(m_networkImageView);

    ui::ImageView* imageLine =  ui::ImageView::create();
    imageLine->loadTexture("image/wifi/wife_line.png");
    imageLine->setPosition(Vec2(m_dateText->getPosition().x + 38,this->getContentSize().height/2));
    this->addChild(imageLine);
    imageLine->setScaleY(0.94f);

    m_cibnImageView = ui::ImageView::create();
    m_cibnImageView->loadTexture("image/other/ic_mainmenu_CIBN.png");
    m_cibnImageView->setPosition(Vec2(150,this->getContentSize().height/2-20));
    this->addChild(m_cibnImageView);

    m_notificationHintImage = ui::ImageView::create();
    m_notificationHintImage->loadTexture(NORTIFICATION_HINT_IMG);
    m_notificationHintImage->setPosition(Vec2(visibleSize.width-320, this->getContentSize().height/2-6));
    m_notificationHintImage->setVisible(false);
    this->addChild(m_notificationHintImage);

    m_notificationCountImage = ui::ImageView::create();
    m_notificationCountImage->setPosition(Vec2(m_notificationHintImage->getPosition().x - 25, m_notificationHintImage->getPosition().y - 8));
    m_notificationCountImage->setVisible(false);
    this->addChild(m_notificationCountImage);

    this->scheduleUpdate();
	return true;
}


void TopBarPanel::updateWifiState(std::string jsonString)
{
	ValueMap wifiStateMap = ParseJson::getIntFromJSON(jsonString);
   int state=  wifiStateMap.at("arg0").asInt();
   if(state == 0xffffffff)
   {
	   return;
   }
   int level =wifiStateMap.at("arg1").asInt();
   log("The network state is--- %d, level is-- %d,-------------------@xjx+++networkState",state,level);
   char  wifiImageFilePath[100];
   sprintf(wifiImageFilePath,NETWORK_WIFI_IMG,level);
   switch(state)
   {
   case kNetwork_State_Disable:
	   m_networkImageView->loadTexture(NETWORK_DISABLE_IMG);
    	break;
   case kNetwork_State_Ethernet:
    	m_networkImageView->loadTexture(NETWORK_NET_IMG);
    	break;
   case kNetwork_State_Wifi:
    	m_networkImageView->loadTexture(NETWORK_WIFI_IMG);
    	break;
   default:
    	break;
    }
}

void TopBarPanel::update(float dt)
{
//		this->updateTimeState();
}
void TopBarPanel::updateTimeState()
{
	//
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

   std::string weekdayString;
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

void TopBarPanel::updateNotificationMessageCountState(int messageCount)
{
	//
	if(messageCount > 0 && messageCount < 4)
	{
	    char  notificationCountImageFilePath[100];
	    sprintf(notificationCountImageFilePath,NOTIFICATION_COUNT_IMG,messageCount);
	    m_notificationCountImage->loadTexture(notificationCountImageFilePath);
	    m_notificationCountImage->setVisible(true);

	    m_notificationHintImage->setVisible(true);
	}
	else
	{
	    m_notificationCountImage->setVisible(false);
	    m_notificationHintImage->setVisible(false);
	}
}

