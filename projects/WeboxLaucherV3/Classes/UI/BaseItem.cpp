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
	m_itemSize = Size::ZERO ;
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
	return true;
}

void BaseItem::setForegroundImage(std::string forgroundImageFilePath)
{
	if(!forgroundImageFilePath.empty())
	{
		m_foregroundImage -> loadTexture(forgroundImageFilePath);
		Size contentSize = m_foregroundImage->getContentSize();
		m_foregroundImage->setPosition(Vec2(m_itemSize.width/2, m_itemSize.height - contentSize.height/2));
	}
}
void BaseItem::setBackgroundImage(std::string backgroundImageFilePath)
{
	if(!backgroundImageFilePath.empty())
	{
		m_backgroundImage -> loadTexture(backgroundImageFilePath);
		m_backgroundImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/2));
	}
}

void BaseItem::setHintText(std::string text)
{
	if(!text.empty())
	{
		m_hintText->setString(text);
		m_hintText->setPosition(Vec2(m_itemSize.width/2,35));
	}
}

 void BaseItem::onEnterClicked(bool isLongPressed)
{
	//....processed this event in the sub-class;
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




