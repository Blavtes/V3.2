/*
 * TopBarLayer.h
 *
 *  Created on: 2014年7月25日
 *      Author: xjx
 */

#ifndef TOPBARPANEL_H_
#define TOPBARPANEL_H_

#include "cocos2d.h"
USING_NS_CC;


class TopBarPanel: public cocos2d::ui::Layout
{
public:
	TopBarPanel();
	virtual ~TopBarPanel();
	static TopBarPanel* create();
	virtual bool init();

	void updateWifiState(std::string jsonString);
	void updateNotificationMessageCountState(int messageCount);
	void updateTimeState();
	void updateWeatherState(std::string jsonString);
private:
    ui::Text* m_timeText;
    ui::Text* m_dateText;
    ui::Text* m_weekdayText;
    ui::ImageView* m_networkImageView;
    ui::ImageView* m_notificationHintImage;
    ui::ImageView* m_notificationCountImage;
    ui::ImageView* m_weatherImg;
    ui::Text* m_weatherStr;
    ui::ImageView* m_cibnImageView;
};

#endif /* TOPBARPANEL_H_ */
