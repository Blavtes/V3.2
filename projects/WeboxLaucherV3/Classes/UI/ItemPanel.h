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
#include "BaseItem.h"
USING_NS_CC;

class ItemPanel: public cocos2d::ui::ScrollView {
public:
	ItemPanel();
	 ~ItemPanel();
	static ItemPanel* create();
	virtual bool init();
	void setItemPanelSize(Size itemPanelSize);
	void setMarginValue(int topMargin, int leftMargin, int middleMargin);

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
	Size m_itemPanelSize;
	int m_topMargin;
	int m_leftMargin;
	int m_middleMargin;
};

#endif /* ITEMPANEL_H_ */
