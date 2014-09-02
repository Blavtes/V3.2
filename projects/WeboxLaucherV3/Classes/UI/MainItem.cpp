
#include "MainItem.h"
USING_NS_CC;

MainItem::MainItem()
{
	m_bottomBackgroundImage = NULL;
	m_itemSize =ITEM_SIZE_MAIN ;
}

MainItem::~MainItem()
{
	// ......
}

bool MainItem::init()
{
	if( !BaseItem::init() )
	{
		return false;
	}
	this->setFocusEnabled(true);
	this->onFocusChanged = CC_CALLBACK_2(MainItem::onFocusChange, this);
	this->setContentSize(m_itemSize);

	m_bottomBackgroundImage = ui::ImageView::create();
	m_bottomBackgroundImage->setPosition(Vec2::ZERO);
	this->addChild(m_bottomBackgroundImage);

	return true;
}

void MainItem::setItemData(ItemData* itemData)
{
	CC_SAFE_RELEASE(m_itemData);
	m_itemData = itemData;
	CC_SAFE_RETAIN(m_itemData);
	float delta = 1;
	if(itemData->getWidth()>delta && itemData->getHeight()>delta)
	{
		if(abs(m_itemSize.width - itemData->getWidth())>delta ||abs(m_itemSize.height - itemData->getHeight())>delta )
		{
			this->setSize(Size(itemData->getWidth(),itemData->getHeight()));
			this->setContentSize(m_itemSize);
		}
	}
	//Update the Item View
	this->setForegroundImage(itemData->getForegroundImageFilePath());
	this->setBackgroundImage(itemData->getBackgroundImageFilePath());
	this->setHintText(itemData->getHintText());
	this->setBottomBackgroundImage(itemData->getBottomBackGroundImageFilePath());
}

void MainItem::setBottomBackgroundImage(std::string bottomBgImageFilePath)
{
	if(!bottomBgImageFilePath.empty())
	{
		m_bottomBackgroundImage->loadTexture(bottomBgImageFilePath);
		Size contentSize = m_bottomBackgroundImage->getContentSize();
		m_bottomBackgroundImage ->setPosition(Vec2(m_itemSize.width/2, contentSize.height/2));
	}
}

void MainItem::onEnterClicked(bool isLongPressed)
{
	log("Call The MainItem!------------------------xjx");
	if(m_itemData->getAction()== "com.starcor.hunan.mgtv.fave")
	{
		JniUtil::startActivityJNI(NULL,"com.togic.mgtv.dynamic","com.togic.mgtv.dynamic.MainActivity");
		return;
	}
	JniUtil::startActivityJNI(m_itemData->getAction().c_str(),NULL,NULL);
}

void MainItem::onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus)
{
	if(widgetLoseFocus != NULL)
	{
		MainItem* mainItemLoseFocus = dynamic_cast<MainItem*>(widgetLoseFocus);
		if(mainItemLoseFocus != NULL)
		{
			//......action
			ScaleTo* action = ScaleTo::create(0.2,1);
			action->setTag(11);
			mainItemLoseFocus->m_foregroundImage->runAction(action);
		}
		widgetLoseFocus->setFocused(false);
	}
	if(widgetGetFocus != NULL)
	{
		MainItem* mainItemGetFocus = dynamic_cast<MainItem*>(widgetGetFocus);
		if(mainItemGetFocus != NULL)
		{
			//......action
			ScaleTo* action = ScaleTo::create(0.2,1.1);
			action->setTag(12);
			mainItemGetFocus->m_foregroundImage->runAction(action);
		}
		widgetGetFocus->setFocused(true);
	}
}



