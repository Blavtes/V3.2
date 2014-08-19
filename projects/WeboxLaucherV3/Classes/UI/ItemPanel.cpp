/*
 * ItemPanel.cpp
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#include "ItemPanel.h"
#include "AppItem.h"
#include "MainItem.h"
#include "NotificationItem.h"
USING_NS_CC;

ItemPanel::ItemPanel()
{
	m_curColumnWidth=0;
	m_itemVector = new Vector<BaseItem*>();
	m_itemPanelSize = ITEM_PANEL_SIZE;
	m_topMargin = MARGIN_TOP;
	m_leftMargin = MARGIN_LEFT;
	m_middleMargin = MARGIN_MIDDLE;
	m_mainItemCount = 0;
}

ItemPanel::~ItemPanel()
{
	delete m_itemVector;
}

ItemPanel* ItemPanel::create()
{
	ItemPanel* itemPanel = new ItemPanel();
	if(itemPanel && itemPanel->init())
	{
		CC_SAFE_RETAIN(itemPanel);
		return itemPanel;
	}
	CC_SAFE_RELEASE(itemPanel);
	return NULL;
}

bool ItemPanel::init()
{
	if(!ScrollView::init())
	{
		return false;
	}
	this->setEnabled(true);
	return true;
}

void ItemPanel::setItemPanelSize(Size itemPanelSize)
{
	//......
	this->ignoreContentAdaptWithSize(false);
	this->setContentSize(itemPanelSize);
	this->setInnerContainerSize(itemPanelSize);
	m_itemPanelSize = itemPanelSize;
}

void ItemPanel::setMarginValue(int topMargin, int leftMargin, int middleMargin)
{
	//
	m_topMargin = topMargin;
	m_leftMargin = leftMargin;
	m_middleMargin = middleMargin;
}


void ItemPanel::addItem(BaseItem* newItem)
{
	if(newItem == NULL)
	{
		log("The added newItem is NULL!");
		return ;
	}
	Vec2 newItemPos;
	Size newItemSize=newItem->getSize();
	int itemCount=m_itemVector->size();
	if(itemCount == 0)
	{                                                                                    //.......................add the first item
		m_curColumnWidth=newItemSize.width;
		newItemPos = Vec2(m_leftMargin + newItemSize.width/2, m_itemPanelSize.height - newItemSize.height/2 - m_topMargin);
	}
	else
	{
		BaseItem* lastItem=m_itemVector->back();
		Vec2  lastItemPos=lastItem->getPosition();
		Size  lastItemSize=lastItem->getSize();
		if( lastItemPos.y  - lastItemSize.height/2 - m_middleMargin  > newItemSize.height)  //.................add item in old Column
		{
			if(newItemSize.width > m_curColumnWidth)            //...............the new item is wider than the former one
			{
				m_curColumnWidth = newItemSize.width;
				newItemPos.x = lastItemPos.x + newItemSize.width/2 - lastItemSize.width/2;
				BaseItem* child=NULL;   //.............move the x-position of the former item in this Column
				for(auto itemV : *m_itemVector)
				{
					child=dynamic_cast<BaseItem*>(itemV);
					if(child->getPosition().x == lastItemPos.x)
					{
						child->setPosition(Vec2(newItemPos.x, child->getPosition().y));
					}
				}
			}
			else                                 //...............the new item is narrow than the former one
			{
				newItemPos.x=lastItemPos.x;
			}
			newItemPos.y=lastItemPos.y- lastItemSize.height/2 - m_middleMargin - newItemSize.height/2;
		}
		else                                      //..........................add item in new Column
		{
			newItemPos.x=lastItemPos.x + m_curColumnWidth/2 + m_middleMargin + newItemSize.width/2;
			newItemPos.y= m_itemPanelSize.height - newItemSize.height/2 - m_topMargin;
			m_curColumnWidth = newItemSize.width;
		}
	}
	newItem->setPosition(newItemPos);

	Size curInnerContainerSize =  this->getInnerContainerSize();//if necessary, change the Size of the container
	if(newItemPos.x + newItemSize.width/2 + m_middleMargin > curInnerContainerSize.width )
	{
		//....
		Size newInnerContainerSize = Size(newItemPos.x + newItemSize.width/2 + m_middleMargin,  curInnerContainerSize.height);
		this->setInnerContainerSize(newInnerContainerSize);
	}
	this->getInnerContainer()->addChild(newItem);// add the item to the panel container
	m_itemVector->pushBack(newItem); //save the item to the vector
	return ;
}

void ItemPanel::removeItemByObject(BaseItem* deletedItem)
{
	if(deletedItem == NULL)
	{
		return;
	}
	int deletedItemIndex = m_itemVector->getIndex(deletedItem);
	if(deletedItemIndex == -1)
	{
		log("The deletedItem is not found in the vector!");
		return;
	}
	this->removeItemByIndex(deletedItemIndex);
}

void ItemPanel::removeItemByIndex(int deletedItemIndex)
{
	if(deletedItemIndex < 0 || deletedItemIndex >= m_itemVector->size())
	{
		log("The deletedItemIndex is out of the vector boundary!");
		return;
	}
	Vector<BaseItem*>  tempVector = *m_itemVector;
	int index = deletedItemIndex;
	while(index < m_itemVector->size())
	{
		BaseItem* item=m_itemVector->at(index);
		this->getInnerContainer()->removeChild(item,true);
		m_itemVector->erase(index);
	}
	for(int i= deletedItemIndex+1; i< tempVector.size(); i++)
	{
		BaseItem* item=tempVector.at(i);
		this->addItem(item);
	}

}

void ItemPanel::insertItemByIndex(BaseItem* newItem, int insertIndex)
{
	//....
	if(insertIndex < 0)
	{
		log("Failed to insert the Item, the index is inValid!");
		return;
	}
	else if(insertIndex >= m_itemVector->size())
	{
		this->addItem(newItem);
	}
	else
	{
		Vector<BaseItem*>  tempVector = *m_itemVector;
		int index = insertIndex;
		while(index < m_itemVector->size())   //------deleted the Item Before the inserted index
		{
			BaseItem* item=m_itemVector->at(index);
			this->getInnerContainer()->removeChild(item,true);
			m_itemVector->erase(index);
		}

		this->addItem(newItem); //add the new Item

		for(int i= insertIndex; i< tempVector.size(); i++)  //----add the deleted index
		{
			BaseItem* item=tempVector.at(i);
			this->addItem(item);
		}
	}
}

int ItemPanel::findItemIndexByItemData(ItemData* itemData)
{
	//
	int destIndex = 0;
	for(int i = 0; i < m_itemVector->size(); i++)
	{
		BaseItem* baseItem = m_itemVector->at(i);
		if(baseItem->getItemData()->getPackage().compare(itemData->getPackage()) == 0)
		{
			return i;
		}
	}
	return -1;
}

void ItemPanel::updateAllItems(Vector<ItemData*> itemVector)
{
	//.......
//	log("update items%zd:===========================@xjx", itemVector.size());
//		for(int i = 0 ; i < itemVector.size(); i++ )
//		{
//			//
//			bool flag = true;
//			ItemData* itemData = itemVector.at(i);
//			for(auto existItem : *m_itemVector)
//			{
//				if(itemData->getID() == existItem->getItemData()->getID() && false)
//				{
//					log("update items===========================@xjx");
//					existItem->setItemData(itemData);
//					flag = false;
//				 }
//			}
//			if(flag)
//			{
//				if(itemData->getCategoryTag().compare("MainItem") == 0 || itemData->getHeight() > 200 )
//				{
//					log("add main items:%d===========================@xjx",i);
//					auto item = MainItem::create();
//					item->setItemData(itemData);
//					this->insertItemByIndex(item,i);
//				}
//				else if(itemData->getCategoryTag().compare("UserItem") == 0 || itemData->getHeight() > 50)
//				{
//					//.......
//					if(itemData->getProFlag() == -1)
//					{
//						//
////						this->removeItemByIndex(clickedItemIndex-1);
//					}
//					else if(itemData->getProFlag() == 1)
//					{
//						log("add app items:%d===========================@xjx",i);
//						auto item = AppItem::create();
//						item->setItemData(itemData);
//						item->setBackgroundImage(APPITEM_FILE_BG_IMG);
//						if(!itemData->getPackage().empty())
//						{
//							log("The foreground image data package is:%s-----------------@xjx",itemData->getPackage().c_str());
//							void* data  = JniUtil::getIconDataWithPackage(itemData->getPackage().c_str());
//							item->setForegroundSpriteByData((void*)data,itemData->getWidth(),itemData->getHeight());
//						}
//						this->addItem(item);
//					}
//				}
//				else
//				{
//					auto item = NotificationItem::create();
//					//..........
//					this->addItem(item);
//				}
//			 }
//		}
}

void ItemPanel::updateMainApps(Vector<ItemData*> itemVector)
{
	//
	if(m_mainItemCount == 0)
	{
		for(int i = 0 ; i < itemVector.size(); i++ )
		{
			log("add main items:%d===========================@xjx",i);
			ItemData* itemData = itemVector.at(i);
			auto item = MainItem::create();
			item->setItemData(itemData);
			this->insertItemByIndex(item,i);
			m_mainItemCount ++;
		}
	}
	else
	{
		for(int i = 0 ; i < itemVector.size(); i++ )
		{
			log("update main items:%d===========================@xjx",i);
			ItemData* itemData = itemVector.at(i);
			if(itemData->getID() == m_itemVector->at(i)->getItemData()->getID() && i <= m_mainItemCount )
			{
				m_itemVector->at(i)->setItemData(itemData);
			}
			else if( i <= m_mainItemCount)
			{
				//
				this->removeItemByIndex(i);
				auto item = MainItem::create();
				item->setItemData(itemData);
				this->insertItemByIndex(item,i);
			}
			else if( i > m_mainItemCount)
			{
				auto item = MainItem::create();
				item->setItemData(itemData);
				this->insertItemByIndex(item,i);
				m_mainItemCount ++;
			}
		}
	}
	if(itemVector.size() < m_mainItemCount)
	{
		for(int i = itemVector.size(); i< m_mainItemCount; i++)
		{
			this->removeItemByIndex(i);
		}
	}
}
void ItemPanel::updateUserApps(Vector<ItemData*> itemVector)
{
	//
	for(int i=0; i<itemVector.size();i++)
	{
		ItemData* itemData = itemVector.at(i);
		if(itemData->getProFlag() == -1)
		{
			log("delete app items:%d===========================@xjx",i);
			int index = this->findItemIndexByItemData(itemData);
			if(index == -1)
			{
				//
				return;
			}
			this->removeItemByIndex(index);
		}
		else if(itemData->getProFlag() == 1)
		{
			log("add app items:%d===========================@xjx",i);
			auto item = AppItem::create();
			item->setItemData(itemData);
			item->setBackgroundImage(APPITEM_FILE_BG_IMG);
			if(!itemData->getPackage().empty())
			{
				log("The foreground image data package is:%s-----------------@xjx",itemData->getPackage().c_str());
				void* data  = JniUtil::getIconDataWithPackage(itemData->getPackage().c_str());
				item->setForegroundSpriteByData((void*)data,itemData->getWidth(),itemData->getHeight());
			}
			this->addItem(item);
		}
	}
}

Vector<BaseItem*>  ItemPanel::getAllItems()
{
	return *m_itemVector;
}

void ItemPanel::addDefaultMainItemByPlistFile(std::string filePath)
{
	if(filePath.empty())
	{
		log("Init MainItem plist file is not exist!");
		return;
	}
    ValueMap map = FileUtils::getInstance()->getValueMapFromFile(filePath);
    ValueMap map1 = map.at("item_tv").asValueMap();
    ValueMap map2 = map.at("item_video").asValueMap();
    ValueMap map3 = map.at("item_album").asValueMap();
    ValueMap map4 = map.at("item_history").asValueMap();

    auto mainItem1 = MainItem::create();
    auto mainItemData1 = ItemData::create();
    mainItemData1->setBackgroundImageFilePath(map1.at("backgroundUrl").asString());
    mainItemData1->setForegroundImageFilePath("");
    mainItemData1->setBottomBackGroundImageFilePath(map1.at("bottomPanelUrl").asString());
    mainItemData1->setHintText(map1.at("hint").asString());
    mainItem1->setItemData(mainItemData1);
   this->addItem(mainItem1);

    auto mainItem2 = MainItem::create();
    auto mainItemData2 = ItemData::create();
    mainItemData2->setBackgroundImageFilePath(map2.at("backgroundUrl").asString());
    mainItemData2->setForegroundImageFilePath(map2.at("topIconUrl").asString());
    mainItemData2->setBottomBackGroundImageFilePath(map2.at("bottomPanelUrl").asString());
    mainItemData2->setHintText(map2.at("hint").asString());
    mainItem2->setItemData(mainItemData2);
   this->addItem(mainItem2);

   auto mainItem3 = MainItem::create();
   auto mainItemData3 = ItemData::create();
   mainItemData3->setBackgroundImageFilePath(map3.at("backgroundUrl").asString());
   mainItemData3->setForegroundImageFilePath(map3.at("topIconUrl").asString());
   mainItemData3->setBottomBackGroundImageFilePath(map3.at("bottomPanelUrl").asString());
   mainItemData3->setHintText(map3.at("hint").asString());
   mainItem3->setItemData(mainItemData3);
  this->addItem(mainItem3);

  auto mainItem4 = MainItem::create();
  auto mainItemData4 = ItemData::create();
  mainItemData4->setBackgroundImageFilePath(map4.at("backgroundUrl").asString());
  mainItemData4->setForegroundImageFilePath(map4.at("topIconUrl").asString());
  mainItemData4->setBottomBackGroundImageFilePath(map4.at("bottomPanelUrl").asString());
  mainItemData4->setHintText(map4.at("hint").asString());
  mainItem4->setItemData(mainItemData4);
 this->addItem(mainItem4);
}

void ItemPanel::addDefaultAppItem()
{
    auto setAppItem = AppItem::create();
    auto setAppItemData = ItemData::create();
    setAppItemData->setBackgroundImageFilePath(APPITEM_SET_BG_IMG);
    setAppItemData->setForegroundImageFilePath(APPITEM_SET_FG_IMG);
    setAppItemData->setHintText(APP_SET_TITLE);
    setAppItemData->setPackage(APP_SET_NAME);
    setAppItem->setItemData(setAppItemData);
    this->addItem(setAppItem);

    auto fileAppItem = AppItem::create();
    auto fileAppItemData = ItemData::create();
    fileAppItemData->setBackgroundImageFilePath(APPITEM_FILE_BG_IMG);
    fileAppItemData->setForegroundImageFilePath(APPITEM_FILE_FG_IMG);
    fileAppItemData->setHintText(APP_FILE_TITLE);
    fileAppItemData->setPackage(APP_FILE_NAME);
    fileAppItem->setItemData(fileAppItemData);
    this->addItem(fileAppItem);

    auto appStoreItem = AppItem::create();
    auto appStoreItemData = ItemData::create();
    appStoreItemData->setBackgroundImageFilePath(APPITEM_APPSTORE_BG_IMG);
    appStoreItemData->setForegroundImageFilePath(APPITEM_APPSTORE_FG_IMG);
    appStoreItemData->setHintText(APP_APPSTORE_TITLE);
    appStoreItemData->setPackage(APP_APPSTORE_NAME);
    appStoreItem->setItemData(appStoreItemData);
    this->addItem(appStoreItem);

    return ;
}

void ItemPanel::autoScrollPanel(int offsetX, ScrollDirection direction)
{
	Vec2 curPos=this->getInnerContainer()->getPosition();
	if(direction == ScrollDirection::Scroll_to_Left)
	{
		this->getInnerContainer()->setPosition(Vec2(curPos.x-offsetX,curPos.y));
	}
	else if(direction == ScrollDirection::Scroll_to_Right)
	{
		this->getInnerContainer()->setPosition(Vec2(curPos.x+offsetX,curPos.y));
	}
}

void ItemPanel::onEnterClicked(int clickedItemIndex, bool isLongPressed) //事件的分开处理
{
	BaseItem* clickedItem = m_itemVector->at(clickedItemIndex-1);
	AppItem* clickedAppItem = dynamic_cast<AppItem*>(clickedItem);
	NotificationItem*  clickedNotificationItem =  dynamic_cast<NotificationItem*>(clickedItem);
	if(clickedAppItem != NULL  && clickedAppItem->getIsUninstalledFlag())
	{
		JniUtil::uninstallSilentJni(clickedItem->getItemData()->getPackage().c_str());
	}
	else if(clickedNotificationItem != NULL)
	{
		this->removeItemByObject(clickedNotificationItem);
	}
	else
	{
		clickedItem->onEnterClicked(isLongPressed);
	}

}









