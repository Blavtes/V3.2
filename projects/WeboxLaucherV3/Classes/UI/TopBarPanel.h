/*
 * TopBarLayer.h
 *
 *  Created on: 2014年7月25日
 *      Author: xjx
 */

#ifndef TOPBARPANEL_H_
#define TOPBARPANEL_H_

#include "cocos2d.h"
#include "../extensions/cocos-ext.h"
USING_NS_CC;
USING_NS_CC_EXT;


class TopBarPanel: public cocos2d::ui::Layout {
public:
	TopBarPanel();
	virtual ~TopBarPanel();
	static TopBarPanel* create();
	virtual bool init();

	void updateWifiState(ValueMap& wifiStateMap);
	void update(float dt);

private:
    ui::Text* m_timeText;
    ui::Text* m_dateText;
    ui::Text* m_weekdayText;
    ui::ImageView* m_networkImageView;
    ui::ImageView* m_cibnImageView;
};

#endif /* TOPBARPANEL_H_ */
