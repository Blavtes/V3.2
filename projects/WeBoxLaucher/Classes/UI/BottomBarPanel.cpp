/*
 * BottomBarPanel.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "BottomBarPanel.h"

BottomBarPanel::BottomBarPanel()
{
	m_cibnText = NULL;
}

BottomBarPanel::~BottomBarPanel()
{
	//........
}

BottomBarPanel* BottomBarPanel::create()
{
	BottomBarPanel* bottomBarPanel = new BottomBarPanel();
	if(bottomBarPanel && bottomBarPanel->init())
	{
		bottomBarPanel->autorelease();
		return bottomBarPanel;
	}
	CC_SAFE_DELETE(bottomBarPanel);
	return NULL;
}

 bool BottomBarPanel::init()
{
	if(!Layout::init())
	{
		return false;
	}
	Size visibleSize = Director::getInstance()->getVisibleSize();
	this->setSize(Size(visibleSize.width-20,80));
	this->setBackGroundColor(Color3B::GREEN);
	this->setBackGroundColorType(BackGroundColorType::SOLID);


	return true;
}
