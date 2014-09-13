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
#include "LeftNotificationPanel.h"
#include "AirPlayPanel.h"
#include "queue"
#include "unistd.h"
USING_NS_CC;

class MainLayer: public cocos2d::Layer
{
public:
	MainLayer();
	virtual ~MainLayer();
	CREATE_FUNC(MainLayer);
	static Scene* createScene();
	virtual bool init();

	void onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event);
	void onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event);
	virtual bool onTouchBegan(Touch *touch, Event *unused_event);
	virtual void onTouchMoved(Touch *touch, Event *unused_event);
	virtual void onTouchEnded(Touch *touch, Event *unused_event);
	virtual void onTouchCancelled(Touch *touch, Event *unused_event);

	void autoStartActivity(float dt);

	void updateCIBNAuthorization(std::string jsonString);
	void updateBackgroundImage(std::string jsonString);
	void showTVItem(std::string jsonString);
	void addTestItems(float dt);
	void update(float dt);
private:
	ui::ImageView* m_backgroundImageView; //background
	ItemPanel* m_itemPanel;
	FocusHelper* m_focusHelper; //middle
	TopBarPanel* m_topBar;  //top
	LeftNotificationPanel* m_notificationPanel; //left according to the Key-Menu
	AirPlayPanel* m_airPlayPanel;
};

#endif /* MAINLAYER_H_ */
