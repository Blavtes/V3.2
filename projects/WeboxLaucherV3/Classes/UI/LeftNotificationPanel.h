/*
 * LeftNotificationPanel.h
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#ifndef LEFTNOTIFICATIONPANEL_H_
#define LEFTNOTIFICATIONPANEL_H_

#include "cocos2d.h"
USING_NS_CC;

class LeftNotificationPanel: public cocos2d::ui::Layout {
public:
	LeftNotificationPanel();
	virtual ~LeftNotificationPanel();
	static LeftNotificationPanel* create();
	virtual bool init();

	void show();

private:
	bool m_showFlag;
	ui::ImageView* m_imageLine;
	ui::ImageView* m_titleImage;
	ui::Text* m_titleText;
	ui::ScrollView* m_itemPanel;
};

#endif /* LEFTNOTIFICATIONPANEL_H_ */
