/*
 * BottomBarPanel.h
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#ifndef BOTTOMBARPANEL_H_
#define BOTTOMBARPANEL_H_

#include "cocos2d.h"
USING_NS_CC;

class BottomBarPanel: public cocos2d::ui::Layout {
public:
	BottomBarPanel();
	virtual ~BottomBarPanel();
	static BottomBarPanel* create();
	virtual bool init();

protected:
	ui::Text* m_cibnText;

};

#endif /* BOTTOMBARPANEL_H_ */
