/*
 * NotificationItem.h
 *
 *  Created on: 2014年7月30日
 *      Author: xjx
 */

#ifndef NOTIFICATIONITEM_H_
#define NOTIFICATIONITEM_H_

#include "BaseItem.h"
USING_NS_CC;

class NotificationItem: public BaseItem {
public:
	NotificationItem();
	virtual ~NotificationItem();
	CREATE_FUNC(NotificationItem);
	virtual bool init();

	void setBackgroundImage(std::string backgroundImageFilePath);
	void setForegroundImage(std::string forgroundImageFilePath);
    void setHintText(std::string hintText);

protected:

};

#endif /* NOTIFICATIONITEM_H_ */
