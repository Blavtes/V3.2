/*
 * NotificationItem.h
 *
 *  Created on: 2014年7月30日
 *      Author: xjx
 */

#ifndef NOTIFICATIONITEM_H_
#define NOTIFICATIONITEM_H_

#include "cocos2d.h"
USING_NS_CC;

class NotificationItem: public cocos2d::ui::Widget {
public:
	NotificationItem();
	virtual ~NotificationItem();
	static NotificationItem* create();
	virtual bool init();

	Size getSize();
	void setSize(Size itemSize);
	void setNotificationImage(std::string notificationImageFilePath);
	void setNotificationText(std::string notificationText);

protected:
	ui::ImageView* m_notificationImage;
	ui::Text* m_notificationText;
	Size m_itemSize;
};

#endif /* NOTIFICATIONITEM_H_ */
