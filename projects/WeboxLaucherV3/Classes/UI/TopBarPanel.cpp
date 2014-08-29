/*
 * TopBarLayer.cpp
 *
 *  Created on: 2014年7月25日
 *      Author: xjx
 */

#include "TopBarPanel.h"
#include "PrefixConst.h"
#include "ToastTextView.h"
#include "Utils/ParseJson.h"
#include "Utils/HandleMessageQueue.h"

TopBarPanel::TopBarPanel() {
    m_timeText = nullptr;
    m_dateText = nullptr;
    m_weekdayText = nullptr;
    m_networkImageView = nullptr;
//    m_cibnImageView = nullptr;
    m_notificationHintImage = nullptr;
    m_notificationCountImage = nullptr;
    m_weatherImg = nullptr;
    m_weatherStr = nullptr;
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
	this->setContentSize(Size(visibleSize.width-20,100));
	Size panelSize = this->getContentSize();

	m_weatherStr = ui::Text::create("", "Arial", 24);
	m_weatherStr->setAnchorPoint(Vec2(1,1));
	m_weatherStr->setPosition(Vec2(panelSize.width-52, panelSize.height-36));
//	m_weatherStr->setFontName( "fonts/FZLTZHK--GBK1-0.ttf");
	m_weatherStr->setFontSize(24);
	m_weatherStr->setColor(Color3B(174, 174, 174));
	m_weatherStr->setString("28 ~ 38");
	m_weatherStr->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_weatherStr->setTextVerticalAlignment(TextVAlignment::CENTER);
	this->addChild(m_weatherStr);

	m_weatherImg = ui::ImageView::create();
	m_weatherImg->setAnchorPoint(Vec2(1,1));
	Vec2 pos = Vec2(m_weatherStr->getPosition().x - 90 -12, panelSize.height-36 );
	m_weatherImg->setPosition(pos);
	m_weatherImg->loadTexture("image/weather/hhh_rain_720.png");
	this->addChild(m_weatherImg);

	ui::ImageView* imageLine =  ui::ImageView::create();
	imageLine->setAnchorPoint(Vec2(1,1));
	imageLine->loadTexture("image/wifi/wife_line.png");
	imageLine->setPosition(Vec2(m_weatherImg->getPosition().x- m_weatherImg->getContentSize().width - 12,panelSize.height-42));
	this->addChild(imageLine);
//	imageLine->setScaleY(0.5f);

	m_timeText = ui::Text::create("", "Arial", 24);
	m_timeText->setAnchorPoint(Vec2(1,1));
	m_timeText->setPosition(Vec2(imageLine->getPosition().x - 12, panelSize.height-36));
	m_timeText->setColor(Color3B(174, 174, 174));
	m_timeText->setFontSize(24);
	m_timeText->setTextHorizontalAlignment(TextHAlignment::CENTER);
	m_timeText->setTextVerticalAlignment(TextVAlignment::CENTER);
	this->addChild(m_timeText);

	m_networkImageView = ui::ImageView::create();
	m_networkImageView->setAnchorPoint(Vec2(1,1));
	m_networkImageView->loadTexture(NETWORK_DISABLE_IMG);
	m_networkImageView->setPosition(Vec2(m_timeText->getPosition().x - 75, panelSize.height-36));
	this->addChild(m_networkImageView);

	m_dateText = ui::Text::create();
	m_dateText->setPosition(Vec2(m_timeText->getPosition().x+90, m_timeText->getPosition().y+10));
	m_dateText->setSize(Size(100, 50));
	m_dateText->enableShadow(Color4B::BLACK,Size(0, -3),3);
	m_dateText->setColor(Color3B(174, 174, 174));
	m_dateText->setFontSize(15);
	m_dateText->setTextHorizontalAlignment( TextHAlignment::CENTER);
	m_dateText->setTextVerticalAlignment(TextVAlignment::CENTER);
	m_dateText ->setVisible(false);
	this->addChild(m_dateText);

	m_weekdayText = ui::Text::create();
	m_weekdayText->setPosition(Vec2(m_dateText->getPosition().x,m_dateText->getPosition().y-20));
	m_weekdayText->setSize(Size(100, 50));
	m_weekdayText->enableShadow(Color4B::BLACK,Size(0, -3),3);
	m_weekdayText->setColor(Color3B(174, 174, 174));
	m_weekdayText->setFontSize(15);
	m_weekdayText->setTextHorizontalAlignment( TextHAlignment::CENTER);
	m_weekdayText->setTextVerticalAlignment(TextVAlignment::CENTER);
	m_weekdayText -> setVisible(false);
	this->addChild(m_weekdayText);

//    m_cibnImageView = ui::ImageView::create();
//    m_cibnImageView->loadTexture("image/other/ic_mainmenu_CIBN.png");
//    m_cibnImageView->setPosition(Vec2(150,this->getContentSize().height/2-20));
//    this->addChild(m_cibnImageView);

	m_notificationHintImage = ui::ImageView::create();
	m_notificationHintImage->loadTexture(NORTIFICATION_HINT_IMG);
	m_notificationHintImage->setPosition(Vec2(visibleSize.width-325, this->getContentSize().height/2-3));
	m_notificationHintImage->setVisible(false);
	this->addChild(m_notificationHintImage);

	m_notificationCountImage = ui::ImageView::create();
	m_notificationCountImage->setPosition(Vec2(m_notificationHintImage->getPosition().x - 25, m_notificationHintImage->getPosition().y - 8));
	m_notificationCountImage->setVisible(false);
	this->addChild(m_notificationCountImage);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(TopBarPanel::updateWifiState,this),"NetworkState");
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(TopBarPanel::updateWeatherState,this),"WeatherState");

	return true;
}


void TopBarPanel::updateWifiState(std::string jsonString)
{
	ValueMap wifiStateMap = ParseJson::getInfoDataFromJSON(jsonString);
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
		if(FileUtils::getInstance()->isFileExist(wifiImageFilePath))
		{
			m_networkImageView->loadTexture(wifiImageFilePath);
		}
		break;
	default:
		break;
	}
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

   std::string weekdayString = "";
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
//	//
//	if(messageCount > 0 && messageCount < 4)
//	{
//	    char  notificationCountImageFilePath[100];
//	    sprintf(notificationCountImageFilePath,NOTIFICATION_COUNT_IMG,messageCount);
//	    m_notificationCountImage->loadTexture(notificationCountImageFilePath);
////	    m_notificationCountImage->setVisible(true);
//
////	    m_notificationHintImage->setVisible(true);
//	}
//	else
//	{
//	    m_notificationCountImage->setVisible(false);
//	    m_notificationHintImage->setVisible(false);
//	}

}


void TopBarPanel::updateWeatherState(std::string jsonString)
{
	log("TopBarPanel::The received message is:%sxaxa---------------------weather",jsonString.c_str());
	ValueMap temperatureMap = ParseJson::getInfoDataFromJSON(jsonString);
	std::string lowTemperature = temperatureMap.at("arg0").asString();
	if(lowTemperature == "")
	{
		return;
	}
	std::string highTemperature = temperatureMap.at("arg1").asString();
	std::string weatherImgFilePath = "image/weather/" +temperatureMap.at("arg2").asString();
	std::string temperature = lowTemperature + "˚~" + highTemperature + "˚";
	m_weatherStr->setString(temperature);
	m_weatherImg->loadTexture(weatherImgFilePath);
}

