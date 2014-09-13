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
#include "Utils/ParseJson.h"
#include "Utils/HandleMessageQueue.h"
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
	m_jsonString = "";
    m_installTogicVideo = false;
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
	this->setDirection(Direction::BOTH);

	this->setTouchEnabled(false);
	auto listenerTouch = EventListenerTouchOneByOne::create();
	listenerTouch->setSwallowTouches(false);
	listenerTouch->onTouchBegan = CC_CALLBACK_2(ItemPanel::onTouchBegan,this);
	listenerTouch->onTouchEnded = CC_CALLBACK_2(ItemPanel::onTouchEnded,this);
	listenerTouch->onTouchMoved = CC_CALLBACK_2(ItemPanel::onTouchMoved,this);
	listenerTouch->onTouchCancelled = CC_CALLBACK_2(ItemPanel::onTouchCancelled,this);
	CCDirector::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listenerTouch,this);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace(); //---------------------------------register msg processing func
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(ItemPanel::updateMainApps,this),"MainApp");
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(ItemPanel::updateUserApps,this),"UserApp");
//	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(ItemPanel::updateMainAppsInfo,this),"MainAppInfo");

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

void ItemPanel::removeItemByIndex(int deletedItemIndex)  //---------after deleted Item, should be re-show the indicator manually
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


void ItemPanel::updateMainApps(std::string jsonString)
{
    //
    m_jsonString = jsonString;
    Vector<ItemData*> itemVector;
    if(!ParseJson::getItemVectorFromJSON(jsonString, itemVector))
    {
        log("ItemPanel:Parse Json String Failed!~~~~~~~~~~~~~~~~~~~~~~~~~~@xjx\n");
        return;
    }

    if(!UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY))
    {
        itemVector.erase(0);
    } else {
#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
        JniUtil::sendBroadcastToSettingShowTV();
#endif
    }

    if (!UserDefault::getInstance()->getBoolForKey(TOGICVIDEO_SHOW_DEFALUT,false)) {
        log("ItemPanel:Add MainApp TOGICVIDEO_SHOW_DEFALUT:%d==",UserDefault::getInstance()->getBoolForKey(TOGICVIDEO_SHOW_DEFALUT,false));
        if (UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY,false)) {
            itemVector.erase(1);
        } else {
            itemVector.erase(0);
        }
    } else {
        log("ItemPanel:Add--->MainApp %d",itemVector.size());
    }

    if(m_mainItemCount == 0)
    {
        for(int i = 0 ; i < itemVector.size(); i++ )
        {
            log("ItemPanel:Add MainApp At The First Time, The MainApp number is:%d===========================@xjx\n",i);
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
            ItemData* itemData = itemVector.at(i);
            if( i < m_mainItemCount)
            {
                //
                if(itemData->getID() == m_itemVector->at(i)->getItemData()->getID() )  //-------------update
                {
                    log("ItemPanel:Update MainApp,The MainApp number is:%d===========================@xjx",i);
                    m_itemVector->at(i)->setItemData(itemData);
                }
                else                                                                                                                                       //----------------replace
                {
                    log("ItemPanel:Replace MainApp,The MainApp number is:%d===========================@xjx",i);
                    this->removeItemByIndex(i);
                    auto item = MainItem::create();
                    item->setItemData(itemData);
                    this->insertItemByIndex(item,i);
                }
            }
            else  																															//-------------------insert
            {
                log("ItemPanel:Insert MainApp,The MainApp number is:%d===========================@xjx",i);
                auto item = MainItem::create();
                item->setItemData(itemData);
                this->insertItemByIndex(item,i);
                m_mainItemCount ++;
            }
        }
    }
    if(itemVector.size() < m_mainItemCount)																		 //--------------------delete
    {
        log("ItemPanel:Delete MainApp.===========================@xjx");
        for(int i = itemVector.size(); i< m_mainItemCount; i++)
        {
            this->removeItemByIndex(i);
        }
        m_mainItemCount =  itemVector.size();
    }
}

void ItemPanel::updateMainAppsInfo(std::string jsonString)
{
	//
	log("ItemPanel:the received updateMainAppsInfo  is %s-----------------------@mainApp",jsonString.c_str());
	ValueMap mainAppInfoMap = ParseJson::getInfoDataFromJSON(jsonString);
	if( mainAppInfoMap.size() < 3)
	{
		log("jsonString parse Failed!");
		return ;
	}
	int index=  mainAppInfoMap.at("arg0").asInt();
	if(!UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY) && index == 0)
	{
		return;
	}
	else if(!UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY))
	{
		index = index -1;
	}
	std::string description =mainAppInfoMap.at("arg1").asString();
	std::string title = mainAppInfoMap.at("arg2").asString();
	for(int i = 0; i<m_mainItemCount; i++)
	{
		MainItem* mainItem =dynamic_cast<MainItem*>( m_itemVector->at(i));
		if(title == "影视")
		{
			if(mainItem != nullptr && mainItem->getItemData()->getAction() == "togic.intent.action.ONLINE_VIDEO")
			{
				mainItem->setHintText(description);
			}
		}
		else if(title == "电视台")
		{
			if(mainItem != nullptr && mainItem->getItemData()->getAction() == "togic.intent.action.LIVE_TV")
			{
				mainItem->setHintText(description);
			}
		}
		else if(title == "观看动态")
		{
			if(mainItem != nullptr && mainItem->getItemData()->getAction() == "togic.intent.action.LIVE_VIDEO_PROGRAM_MY_FAVOR")
			{
				mainItem->setHintText(description);
			}
		}
		else if(title == "")
		{
			if(mainItem != nullptr && mainItem->getItemData()->getAction() == "togic.intent.action.ALBUM")
			{
				mainItem->setHintText(description);
			}
		}
	}
//	if(index >=0 && index < m_mainItemCount)
//	{
//		MainItem* mainItem =dynamic_cast<MainItem*>( m_itemVector->at(index));
//		if(nullptr != mainItem)
//		{
//			ItemData* mainItemData = mainItem->getItemData();
//			mainItemData->setDescription(description);
//			mainItemData->setTitle(title);
//			mainItem->setHintText(description);
//		}
//	}
}

void ItemPanel::updateUserApps(string jsonString)
{
//<<<<<<< HEAD
    //
    Vector<ItemData*> itemVector;
    if(!ParseJson::getItemVectorFromJSON(jsonString, itemVector))
    {
        log("ItemPanel:Parse Json String Failed!~~~~~~~~~~~~~~~~~~~~~~~~~~@xjx\n");
        return;
    }
    int m_index = m_itemVector->size()+1;
    std::string backgroundImageFilePaths[]={APPITEM_FILE_BG_IMG, APPITEM_SET_BG_IMG, APPITEM_APPSTORE_BG_IMG};
    for(int i=0; i<itemVector.size();i++)
    {
        ItemData* itemData = itemVector.at(i);
        if(itemData->getProFlag() == -1)
        {
            log("ItemPanel:Delete UserApp,The UserApp number is:%d===========================@xjx",i);
            int index = this->findItemIndexByItemData(itemData);
            if(index == -1)
            {
                continue;
            }
            this->removeItemByIndex(index);
        }
        else if(itemData->getProFlag() == 1)
        {
            log("ItemPanel:Add UserApp , The UserApp number is:%d===========================@xjx\n",i);
            if (itemData->getPackage().compare("com.togic.livevideo") == 0) {
                if (m_installTogicVideo == false) {
                    m_installTogicVideo = true;
                } else {

                    ItemData * data = ItemData::create();
                    data->setAction("togic.intent.action.ONLINE_VIDEO");
                    data->setProFlag(0);
                    data->setBackgroundImageFilePath("image/item/item_togic_bg.png");
                    data->setWidth(260);
                    data->setHeight(432);
                    auto item = MainItem::create();
                    item->setItemData(data);
                    if (UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY,false)  ==  false ) {
                        this->insertItemByIndex(item,0);
                    } else if (UserDefault::getInstance()->getBoolForKey(USER_SHOW_TV_KEY,false)  ==  true) {
                        this->insertItemByIndex(item,1);
                    } else {
                                            this->insertItemByIndex(item,0);
                    }
                    UserDefault::getInstance()->setBoolForKey(TOGICVIDEO_SHOW_DEFALUT, true);
                    UserDefault::getInstance()->flush();
                    m_mainItemCount++;
                }
                continue;
            }
            auto item = AppItem::create();
            item->setItemData(itemData);
            char a[100];
            sprintf(a,"image/appitem/app_bg_%d.png",(i+m_index)%5);
            item->setBackgroundImage(a);
            if(!itemData->getPackage().empty())
            {
                void* data  = JniUtil::getIconDataWithPackage(itemData->getPackage().c_str()); //------Get Image Data from Network
                if(data == NULL)
                {
                    return;
                }
                item->setForegroundSpriteByData((void*)data,itemData->getWidth(),itemData->getHeight());
            }
            this->addItem(item);
        }
    }
//=======
//	//
//	Vector<ItemData*> itemVector;
//	if(!ParseJson::getItemVectorFromJSON(jsonString, itemVector))
//	{
//		log("ItemPanel:Parse Json String Failed!~~~~~~~~~~~~~~~~~~~~~~~~~~@xjx\n");
//		return;
//	}
//
//	for(int i=0; i<itemVector.size();i++)
//	{
//		ItemData* itemData = itemVector.at(i);
//		if(itemData->getProFlag() == -1)
//		{
//			log("ItemPanel:Delete UserApp,The UserApp number is:%d===========================@xjx",i);
//			int index = this->findItemIndexByItemData(itemData);
//			if(index == -1)
//			{
//				return;
//			}
//			this->removeItemByIndex(index);
//		}
//		else if(itemData->getProFlag() == 1)
//		{
//			log("ItemPanel:Add UserApp , The UserApp number is:%d===========================@xjx\n",i);
//			int index = this->findItemIndexByItemData(itemData);
//			if(index != -1)
//			{
//				return;
//			}
//			auto item = AppItem::create();
//			item->setItemData(itemData);
//			char a[100];
//			sprintf(a,"image/appitem/app_bg_%d.png",i%5);
//			item->setBackgroundImage(a);
//			if(!itemData->getPackage().empty())
//			{
//				void* data  = JniUtil::getIconDataWithPackage(itemData->getPackage().c_str()); //------Get Image Data from Network
//				if(data == NULL)
//				{
//					return;
//				}
//				item->setForegroundSpriteByData((void*)data,itemData->getWidth(),itemData->getHeight());
//			}
//			this->addItem(item);
//		}
//	}
//>>>>>>> rebase cibn 0913.
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
	m_mainItemCount++;

	auto mainItem2 = MainItem::create();
	auto mainItemData2 = ItemData::create();
	mainItemData2->setBackgroundImageFilePath(map2.at("backgroundUrl").asString());
	mainItemData2->setForegroundImageFilePath(map2.at("topIconUrl").asString());
	mainItemData2->setBottomBackGroundImageFilePath(map2.at("bottomPanelUrl").asString());
	mainItemData2->setHintText(map2.at("hint").asString());
	mainItem2->setItemData(mainItemData2);
	this->addItem(mainItem2);
	m_mainItemCount++;

	   auto mainItem3 = MainItem::create();
	   auto mainItemData3 = ItemData::create();
	   mainItemData3->setBackgroundImageFilePath(map3.at("backgroundUrl").asString());
	   mainItemData3->setForegroundImageFilePath(map3.at("topIconUrl").asString());
	   mainItemData3->setBottomBackGroundImageFilePath(map3.at("bottomPanelUrl").asString());
	   mainItemData3->setHintText(map3.at("hint").asString());
	   mainItem3->setItemData(mainItemData3);
	  this->addItem(mainItem3);
	  m_mainItemCount++;

	  auto mainItem4 = MainItem::create();
	  auto mainItemData4 = ItemData::create();
	  mainItemData4->setBackgroundImageFilePath(map4.at("backgroundUrl").asString());
	  mainItemData4->setForegroundImageFilePath(map4.at("topIconUrl").asString());
	  mainItemData4->setBottomBackGroundImageFilePath(map4.at("bottomPanelUrl").asString());
	  mainItemData4->setHintText(map4.at("hint").asString());
	  mainItem4->setItemData(mainItemData4);
	 this->addItem(mainItem4);
	 m_mainItemCount++;

// auto mainItem5 = MainItem::create();
// auto mainItemData5 = ItemData::create();
// mainItemData5->setBackgroundImageFilePath(map4.at("backgroundUrl").asString());
// mainItemData5->setForegroundImageFilePath(map4.at("topIconUrl").asString());
// mainItemData5->setBottomBackGroundImageFilePath(map4.at("bottomPanelUrl").asString());
// mainItemData5->setHintText(map4.at("hint").asString());
// mainItem5->setItemData(mainItemData5);
//this->addItem(mainItem5);
//m_mainItemCount++;
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


void ItemPanel::onEnterClicked(int clickedItemIndex, bool isLongPressed) //事件的分开处理
{
	if(clickedItemIndex <=0 || clickedItemIndex > m_itemVector->size())
	{
		log("clickedItemIndex is invalid!!!!!!!---------@show");
		return;
	}
	BaseItem* clickedItem = m_itemVector->at(clickedItemIndex-1);
	AppItem* clickedAppItem = dynamic_cast<AppItem*>(clickedItem);
	NotificationItem*  clickedNotificationItem =  dynamic_cast<NotificationItem*>(clickedItem);
	if(clickedAppItem != NULL  && clickedAppItem->getIsUninstalledFlag())
	{
		JniUtil::uninstallSilentJni(clickedItem->getItemData()->getPackage().c_str());
	}
	else if(clickedNotificationItem != NULL)
	{
		clickedNotificationItem->onEnterClicked(false);
//		if(clickedNotificationItem->getItemData()->getCode() == 10)
//		{
//			this->removeItemByObject(clickedNotificationItem);
//		}
	}
	else
	{
		clickedItem->onEnterClicked(isLongPressed);
	}

}

int ItemPanel::getMainItemCount()
{
	return m_mainItemCount;
}

void ItemPanel::showTVItem(std::string jsonString)
{
	//
	UserDefault::getInstance()->setBoolForKey(USER_SHOW_TV_KEY,true);
	Vector<ItemData*> itemVector;
	if(!ParseJson::getItemVectorFromJSON(m_jsonString, itemVector))
	{
		log("ItemPanel:Parse Json String Failed!~~~~~~~~~~~~~~~~~~~~~~~~~~@xjx\n");
		return;
	}
	MainItem* mainItem = MainItem::create();
	mainItem->setItemData(itemVector.at(0));
	this->insertItemByIndex(mainItem,0);

}








