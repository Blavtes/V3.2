/*
 * MainLayer.h
 *
 *  Created on: 2014年7月16日
 *      Author: xjx
 */

#ifndef MAINLAYER_H_
#define MAINLAYER_H_

#include "cocos2d.h"
#include "ItemPanel.h"
#include "FocusHelper.h"
#include "TopBarPanel.h"
#include "BottomBarPanel.h"
#include "LeftNotificationPanel.h"
#include "../extensions/cocos-ext.h"
USING_NS_CC;
USING_NS_CC_EXT;

class MainLayer: public cocos2d::Layer {
public:
	MainLayer();
	virtual ~MainLayer();
	CREATE_FUNC(MainLayer);
	static Scene* createScene();
	virtual bool init();
    void onKeyPressed(EventKeyboard::KeypadEventCode keyCode, EventKeyboard::KeypadEventType eventType, Event* event);
    void onKeyReleased(EventKeyboard::KeypadEventCode keyCode, EventKeyboard::KeypadEventType eventType, Event* event);

	void CIBNAuthorization(ValueMap&  map);


private:
	ItemPanel* m_itemPanel;
	FocusHelper* m_focusHelper;
	TopBarPanel* m_topBar;
	BottomBarPanel* m_bottomBar;
	ui::ImageView* m_backgroundImageView;
	ui::Text* m_cibnText;

	LeftNotificationPanel* m_notificationPanel;

};

#endif /* MAINLAYER_H_ */
