/*
 * BaseItem.h
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#ifndef BASEITEM_H_
#define BASEITEM_H_

#include "cocos2d.h"
#include "PrefixConst.h"
#include "Data/ItemData.h"
USING_NS_CC;

class BaseItem: public cocos2d::ui::Widget {
public:
	BaseItem();
	virtual ~BaseItem();
	CREATE_FUNC(BaseItem);
	virtual bool init();

	Size getSize();
	void setSize(Size itemSize);
	virtual void setItemData(ItemData* itemData);
	virtual ItemData* getItemData();

	virtual void setForegroundImage(std::string forgroundImageFilePath);
	virtual void setBackgroundImage(std::string backgroundImageFilePath);
	virtual void setHintText(std::string hintText);

	virtual void onEnterClicked(bool isLongPressed);
protected:
	ui::ImageView* m_backgroundImage; //top background image
	ui::ImageView*  m_foregroundImage;//top foreground image
	ui::Text* m_hintText; //bottom hint text

	Size m_itemSize;//item size
	ItemData* m_itemData;
};

#endif /* BASEITEM_H_ */
