/*
 * LeftNotificationPanel.h
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#ifndef LEFTNOTIFICATIONPANEL_H_
#define LEFTNOTIFICATIONPANEL_H_

#include "cocos2d.h"
#include "ItemPanel.h"
#include "FocusHelper.h"
#include "ReceiveMessageProtocol.h"
USING_NS_CC;

class LeftNotificationPanel: public cocos2d::ui::Layout , public ReceiveMessageProtocol
{
public:
	LeftNotificationPanel();
	virtual ~LeftNotificationPanel();
	static LeftNotificationPanel* create();
	virtual bool init();

	bool getLeftNotificationPanelStatus();
    void onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event);
    void onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event);
	void show();
	void addTestItems();

	void receiveMessageData(std::string messageTitle, std::string jsonString);
private:
	bool m_statusFlag;
	ui::ImageView* m_imageLine;
	ui::ImageView* m_titleImage;
	ui::Text* m_titleText;
	ItemPanel* m_itemPanel;
	FocusHelper* m_focusHelper;
	int m_notificationCount;
};

#endif /* LEFTNOTIFICATIONPANEL_H_ */
