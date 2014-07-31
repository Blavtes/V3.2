
#include "MainItem.h"
USING_NS_CC;
USING_NS_CC_EXT;

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
}

void MainItem::onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus)
{
	log("the MainItem function onFocusChanged() is called automatically!--------------------------xjx");
	if(widgetLoseFocus != NULL)
	{
		MainItem* mainItemLoseFocus = dynamic_cast<MainItem*>(widgetLoseFocus);
		if(mainItemLoseFocus != NULL)
		{
			//......action
			ScaleTo* action = ScaleTo::create(0.2,1);
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
			mainItemGetFocus->m_foregroundImage->runAction(action);
		}
		widgetGetFocus->setFocused(true);
	}
}



