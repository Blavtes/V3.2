
#include "MainItem.h"
USING_NS_CC;
USING_NS_CC_EXT;

MainItem::MainItem()
{
	m_bottomBackgroundImage = NULL;
	m_itemSize =ITEM_SIZE_MAIN ;
	m_bottomSize = BOTTOM_PART_SIZE_MAIN;
	m_topSize = TOP_PART_SIZE_MAIN;
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

	m_bottomBackgroundImage = ui::ImageView::create();
	m_bottomBackgroundImage->setPosition(Vec2::ZERO);
	this->addChild(m_bottomBackgroundImage);

	return true;
}

void MainItem::setBottomBackgroundImage(std::string bottomBgImageFilePath,Size bottomContentSize)
{
	if(!bottomBgImageFilePath.empty())
	{
		m_bottomBackgroundImage->loadTexture(bottomBgImageFilePath);
		this->setPositionAndSize(m_bottomBackgroundImage,bottomContentSize,false);
	}
}

void MainItem::setForegroundImage(std::string forgroundImageFilePath,Size contentSize)
{
	if(!forgroundImageFilePath.empty())
	{
		m_foregroundImage -> loadTexture(forgroundImageFilePath);
		this->setPositionAndSize(m_foregroundImage,contentSize,true);
	}
}
void MainItem::setBackgroundImage(std::string backgroundImageFilePath,Size contentSize)
{
	if(!backgroundImageFilePath.empty())
	{
		m_backgroundImage -> loadTexture(backgroundImageFilePath);
		this->setPositionAndSize(m_backgroundImage,contentSize,true);
	}
}


void MainItem::updateSize()
{
	m_itemSize=Vec2(m_topSize.width>m_bottomSize.width?m_topSize.width:m_bottomSize.width,m_topSize.height+m_bottomSize.height-15);//-15
	this->setContentSize(m_itemSize);
	this->setSize(m_itemSize);
}

void MainItem::setPositionAndSize(ui::Widget* widget, Size contentSize,bool isTopPart)
{
	Size widgetSize=widget->getContentSize();
	if(isTopPart)
	{
		m_topSize = contentSize;
		this->setContentSize(m_topSize);
		widget->setScaleX(m_topSize.width/widgetSize.width);
		widget->setScaleY(m_topSize.height/widgetSize.height);
		widget->setPosition(Vec2(m_topSize.width/2,m_topSize.height/2+m_bottomSize.height));
	}
	else
	{
		m_bottomSize = contentSize;
		this->setContentSize(m_bottomSize);
		widget->setScaleX(m_bottomSize.width/widgetSize.width);
		widget->setScaleY(m_bottomSize.height/widgetSize.height);
		widget->setPosition(Vec2(m_bottomSize.width/2,m_bottomSize.height/2+15));//+15
		this->updateSize();
	}
}

void MainItem::setHintText(std::string hintText)
{
	//
	if(!hintText.empty())
	{
		m_hintText->setString(hintText);
		m_hintText->setPosition(Vec2(m_bottomSize.width/2,m_bottomSize.height/3));
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



