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
#include "Data/ItemData.h"
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
	void insertItemByIndex(BaseItem* newItem, int index);
	int findItemIndexByItemData(ItemData* item);
	Vector<BaseItem*>  getAllItems();
	void updateMainApps(std::string jsonString);
	void updateMainAppsInfo(std::string jsonString);
	void updateUserApps(std::string jsonString);
	void showTVItem(std::string jsonString);
	void onEnterClicked(int clickedItemIndex, bool isLongPressed);

	void addDefaultMainItemByPlistFile(std::string filePath);
	void addDefaultAppItem();
	int getMainItemCount();
private:
	Vector<BaseItem*> * m_itemVector;
	int m_curColumnWidth;
	Size m_itemPanelSize;
	int m_topMargin;
	int m_leftMargin;
	int m_middleMargin;
	int m_mainItemCount;
	std::string m_jsonString;
    bool m_installTogicVideo;
};

#endif /* ITEMPANEL_H_ */
