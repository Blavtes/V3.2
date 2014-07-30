/*
 * BaseItem.cpp
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#include "BaseItem.h"
USING_NS_CC;
USING_NS_CC_EXT;

BaseItem::BaseItem() {
	// ........
	m_foregroundImage = NULL;
	m_backgroundImage = NULL;
	m_hintText = NULL;
	m_itemSize = Vec2::ZERO ;
	m_itemName = "";
}

BaseItem::~BaseItem() {
	// ......
}

bool BaseItem::init()
{
	if( !Widget::init() )
	{
		return false;
	}

	m_backgroundImage = ui::ImageView::create();
	m_backgroundImage->setPosition(Vec2::ZERO);
	this->addChild(m_backgroundImage,0);

	m_foregroundImage = ui::ImageView::create();
	m_foregroundImage->setPosition(Vec2::ZERO);
	 this->addChild(m_foregroundImage,1);

	 m_hintText = ui::Text::create("","Arial",28);
	 m_hintText->setPosition(Vec2::ZERO);
	 this->addChild(m_hintText,2);

	this->setContentSize(m_itemSize);
	return true;
}

void BaseItem::setForegroundImage(std::string forgroundImageFilePath,Size contentSize)
{
	if(!forgroundImageFilePath.empty())
	{
		m_foregroundImage -> loadTexture(forgroundImageFilePath);
		this->setPositionAndSize(m_foregroundImage,contentSize,false);
	}
}
void BaseItem::setBackgroundImage(std::string backgroundImageFilePath,Size contentSize)
{
	if(!backgroundImageFilePath.empty())
	{
		m_backgroundImage -> loadTexture(backgroundImageFilePath);
		this->setPositionAndSize(m_backgroundImage,contentSize,false);
	}
}

void BaseItem::setHintText(std::string text)
{
	//........
}

 void BaseItem::onEnterClicked(bool isLongPressed)
{
	//....processed this event in the sub-class;
}

void BaseItem::setPositionAndSize(ui::Widget* widget, Size contentSize,bool isTopPart)
{
	if(widget == NULL)
	{
		return ;
	}
	Size widgetSize=widget->getContentSize();
	m_itemSize = contentSize;
	this->setContentSize(m_itemSize);
	widget->setScaleX(m_itemSize.width/widgetSize.width);
	widget->setScaleY(m_itemSize.height/widgetSize.height);
	widget->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/2));
}

Size BaseItem::getSize()
{
	return m_itemSize;
}
void BaseItem::setSize(Size itemSize)
{
	m_itemSize = itemSize;
}

void BaseItem::setItemName(std::string itemName)
{
	m_itemName = itemName;
}

std::string BaseItem::getItemName()
{
	return m_itemName;
}




