/*
 * BaseItem.h
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#ifndef BASEITEM_H_
#define BASEITEM_H_



#include "../extensions/cocos-ext.h"
#include "cocos2d.h"
#include "PrefixConst.h"

USING_NS_CC;
USING_NS_CC_EXT;

class BaseItem: public cocos2d::ui::Widget {
public:
	BaseItem();
	virtual ~BaseItem();
	CREATE_FUNC(BaseItem);
	virtual bool init();

	Size getSize();
	void setSize(Size itemSize);
	void setItemName(std::string itemName);
	std::string getItemName();

	virtual void setForegroundImage(std::string forgroundImageFilePath,Size contentSize);
	virtual void setBackgroundImage(std::string backgroundImageFilePath,Size contentSize);
	virtual void setHintText(std::string hintText);
	virtual void setPositionAndSize(ui::Widget* widget, Size contentSize,bool isTopPart);

	virtual void onEnterClicked(bool isLongPressed);

protected:
	ui::ImageView* m_backgroundImage; //top background image
	ui::ImageView*  m_foregroundImage;//top foreground image
	ui::Text* m_hintText; //bottom hint text
	Size m_itemSize;//item size
	std::string m_itemName; //identify the item
};

#endif /* BASEITEM_H_ */
