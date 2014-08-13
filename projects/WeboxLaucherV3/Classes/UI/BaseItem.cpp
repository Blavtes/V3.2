/*
 * BaseItem.cpp
 *
 *  Created on: 2014年7月10日
 *      Author: xjx
 */

#include "BaseItem.h"
USING_NS_CC;

BaseItem::BaseItem()
{
	m_foregroundImage = NULL;
	m_backgroundImage = NULL;
	m_hintText = NULL;
	m_itemSize = Size::ZERO ;
	m_itemData = nullptr;
}

BaseItem::~BaseItem()
{
	// ......
	CC_SAFE_RELEASE(m_itemData);
}

bool BaseItem::init()
{
	if( !Widget::init() )
	{
		return false;
	}
	this->setContentSize(m_itemSize);
	m_backgroundImage = ui::ImageView::create();
	m_backgroundImage->setPosition(Vec2::ZERO);
	this->addChild(m_backgroundImage,0);

	m_foregroundImage = ui::ImageView::create();
	m_foregroundImage->setPosition(Vec2::ZERO);
	this->addChild(m_foregroundImage,1);

	m_hintText = ui::Text::create("","Arial",28);
	m_hintText->setPosition(Vec2::ZERO);
	this->addChild(m_hintText,2);

	m_itemData = ItemData::create();
	m_itemData->retain();
	return true;
}

void BaseItem::setItemData(ItemData* itemData)
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
}

ItemData* BaseItem::getItemData()
{
	return m_itemData;
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
		Size backgroundImageSize = m_backgroundImage->getContentSize();
		m_backgroundImage->setScale(m_itemSize.width/backgroundImageSize.width,m_itemSize.height/backgroundImageSize.height);
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





