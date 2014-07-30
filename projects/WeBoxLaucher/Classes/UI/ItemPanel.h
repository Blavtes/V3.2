/*
 * ItemPanel.h
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#ifndef ITEMPANEL_H_
#define ITEMPANEL_H_

#include  "PrefixConst.h"
#include "cocos2d.h"
#include "../extensions/cocos-ext.h"
#include "BaseItem.h"


USING_NS_CC;
USING_NS_CC_EXT;


class ItemPanel: public cocos2d::ui::ScrollView {
public:
	ItemPanel();
	 ~ItemPanel();
	static ItemPanel* create();
	virtual bool init();

	void addItem(BaseItem* newItem);
	void removeItemByObject(BaseItem* deletedItem);
	void removeItemByIndex(int deletedItemIndex);
	void updateAllItems();
	Vector<BaseItem*>  getAllItems();
	void addDefaultMainItemByPlistFile(std::string filePath);
	void addDefaultAppItem();

	void autoScrollPanel(int offsetX, ScrollDirection direction);
	void onEnterClicked(int clickedItemIndex, bool isLongPressed);

private:
	Vector<BaseItem*> * m_itemVector;
	int m_curColumnWidth;
};

#endif /* ITEMPANEL_H_ */
