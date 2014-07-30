/*
 * ItemPanel.cpp
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#include "ItemPanel.h"
#include "AppItem.h"
#include "MainItem.h"


USING_NS_CC;

ItemPanel::ItemPanel()
{
	m_curColumnWidth=0;
	m_itemVector = new Vector<BaseItem*>();
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
	Size visibleSize = CCDirector::getInstance()->getVisibleSize();
	this->setSize(Size(visibleSize.width-20, ITEM_PANEL_SIZE_HEIGHT));
	this->setInnerContainerSize(this->getSize());
	this->setEnabled(true);
	this->setDirection(Direction::HORIZONTAL);
//	this->setBackGroundColor(Color3B::BLUE);
//	this->setBackGroundColorType(BackGroundColorType::SOLID);

    auto item1= MainItem::create();
    item1->setForegroundImage("top-forground-1.png");
    item1->setBackgroundImage("top-background-1.png");
    item1->setBottomBackgroundImage("bottom-bac-1.png");
    item1->setHintText("Hello World!");
    this->addItem(item1);


    auto item2= MainItem::create();
    item2->setForegroundImage("top-forground-2.png");
    item2->setBackgroundImage("top-background-2.png");
    item2->setBottomBackgroundImage("bottom-bac-2.png");
    item2->setHintText("Hello World!");
    this->addItem(item2);

    auto item3= MainItem::create();
    item3->setForegroundImage("top-forground-3.png");
    item3->setBackgroundImage("top-background-3.png");
    item3->setBottomBackgroundImage("bottom-bac-3.png");
    item3->setHintText("Hello World!");
    this->addItem(item3);

   auto item4=AppItem::create();
   item4->setBackgroundImage("app_bg_2.png");
   item4->setForegroundImage("app_settingicon.png");
   item4->setHintText("app 1");
   this->addItem(item4);

   auto item5=AppItem::create();
   item5->setBackgroundImage("app_bg_1.png");
   item5->setForegroundImage("app_fileicon.png");
   item5->setHintText("app 2");
   this->addItem(item5);

   auto item6=AppItem::create();
   item6->setBackgroundImage("app_bg_1.png");
   item6->setForegroundImage("app_fileicon.png");
   item6->setHintText("app 3");
   this->addItem(item6);

   auto item7=AppItem::create();
   item7->setBackgroundImage("app_bg_1.png");
   item7->setForegroundImage("app_fileicon.png");
   item7->setHintText("app 4");
   this->addItem(item7);

   auto item8=AppItem::create();
   item8->setBackgroundImage("app_bg_2.png");
   item8->setForegroundImage("app_settingicon.png");
   item8->setHintText("app 5");
   this->addItem(item8);

   auto item9=AppItem::create();
   item9->setBackgroundImage("app_bg_2.png");
   item9->setForegroundImage("app_settingicon.png");
   item9->setHintText("app 6");
   this->addItem(item9);

    std::string plistFilePath= std::string("plist/mainData.plist");
    this->addDefaultMainItemByPlistFile(plistFilePath);
    this->addDefaultAppItem();
	return true;
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
		newItemPos = Vec2(MARGIN_LEFT+newItemSize.width/2, ItemPanelSize.height - newItemSize.height/2 - MARGIN_TOP);
	}
	else
	{
		BaseItem* lastItem=m_itemVector->back();
		Vec2  lastItemPos=lastItem->getPosition();
		Size  lastItemSize=lastItem->getSize();
		if( lastItemPos.y  - lastItemSize.height/2 - MARGIN_MIDDLE  > newItemSize.height)  //.................add item in old Column
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
			newItemPos.y=lastItemPos.y- lastItemSize.height/2 - MARGIN_MIDDLE - newItemSize.height/2;
		}
		else                                      //..........................add item in new Column
		{
			newItemPos.x=lastItemPos.x+m_curColumnWidth/2+MARGIN_MIDDLE+newItemSize.width/2;
			newItemPos.y= ItemPanelSize.height - newItemSize.height/2 - MARGIN_TOP;
			m_curColumnWidth = newItemSize.width;
		}
	}
	newItem->setPosition(newItemPos);

	Size curInnerContainerSize =  this->getInnerContainerSize();//if necessary, change the Size of the container
	if(newItemPos.x + newItemSize.width/2 + MARGIN_MIDDLE > curInnerContainerSize.width )
	{
		//....
		Size newInnerContainerSize = Size(newItemPos.x + newItemSize.width/2 + MARGIN_MIDDLE,  curInnerContainerSize.height);
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
	if(deletedItemIndex < 0 || deletedItemIndex > m_itemVector->size())
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

void ItemPanel::updateAllItems()
{
	//.......
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

    auto mainItem2 = MainItem::create();
    mainItem2->setForegroundImage(map2.at("topIconUrl").asString());
    mainItem2->setBackgroundImage(map2.at("backgroundUrl").asString());
    mainItem2->setBottomBackgroundImage(map2.at("bottomPanelUrl").asString());
    mainItem2->setHintText(map2.at("hint").asString());
   this->addItem(mainItem2);

}

void ItemPanel::addDefaultAppItem()
{
    auto setAppItem = AppItem::create();
    setAppItem->setForegroundImage(APPITEM_SET_FG_IMG);
    setAppItem->setBackgroundImage(APPITEM_SET_BG_IMG);
    setAppItem->setHintText(APP_SET_TITLE);
    setAppItem->setItemName(APP_SET_NAME);
    this->addItem(setAppItem);

    auto fileAppItem = AppItem::create();
    fileAppItem->setForegroundImage(APPITEM_FILE_FG_IMG);
    fileAppItem->setBackgroundImage(APPITEM_FILE_BG_IMG);
    fileAppItem->setHintText(APP_FILE_TITLE);
    fileAppItem->setItemName(APP_FILE_NAME);
    this->addItem(fileAppItem);

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
	if(clickedAppItem != NULL  && clickedAppItem->getIsUninstalledFlag())
	{
		this->removeItemByIndex(clickedItemIndex-1);
	}
	else
	{
		clickedItem->onEnterClicked(isLongPressed);
	}

}









