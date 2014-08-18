/*
 * FocusHelper.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "FocusHelper.h"

FocusHelper::FocusHelper()
{
	m_itemView = NULL;
	m_focusIndicator = NULL;
	m_focusPaddingX = 31;
	m_focusPaddingY = 31;
	m_selectedItemIndex =0;
	m_contentOffsetX = 0;
	m_pressedCount = 0;
	m_pressedFlag = true;
}

FocusHelper::~FocusHelper()
{
	// .......
}

 FocusHelper* FocusHelper::create()
{
	FocusHelper* focusHelper = new FocusHelper();
	if(focusHelper && focusHelper->init())
	{
		focusHelper->autorelease();
		return focusHelper;
	}
	CC_SAFE_DELETE(focusHelper);
	return NULL;
}

 bool FocusHelper::init()
{
	 //.................
	return true;
}

void FocusHelper::bindItemPanel(ItemPanel* itemPanel,std::string focusIndicatorImageFilePath)
{
	if(itemPanel == nullptr)
	{
		log("The bind ItemPanel is null !----------xjx");
		return ;
	}
	else
	{
		CC_SAFE_RETAIN(itemPanel);
		CC_SAFE_RELEASE(m_itemView);
		m_itemView = itemPanel;
		if(m_itemView->getAllItems().size() > 0 )
		{
			this->initFocusIndicator(focusIndicatorImageFilePath);
		}
	}
}

void FocusHelper::initFocusIndicator(std::string focusIndicatorImageFilePath)
{
		BaseItem* frontItem	=  m_itemView->getAllItems().front();
		frontItem->setFocused(true);
		Size itemSize = frontItem->getSize();
		Vec2  pos =frontItem->getPosition();
		this->onFocusChanged(nullptr,frontItem);

		m_focusIndicator = Scale9Sprite::create(focusIndicatorImageFilePath);  //Init The Focus Indicator
		Rect capInsets=Rect(0,0,m_focusPaddingX,m_focusPaddingY);
		m_focusIndicator->resizableSpriteWithCapInsets(capInsets);
		m_focusIndicator->setContentSize(Size(itemSize.width + m_focusPaddingX*2,itemSize.height +m_focusPaddingY*2));
		m_focusIndicator->setPosition(pos);
		m_itemView->addChild(m_focusIndicator,2);
		m_selectedItemIndex = 1;

		return ;
}

int FocusHelper::getSelectedItemIndex()
{
	return m_selectedItemIndex;
}


void FocusHelper::moveFocusIndicatorToRight()
{
	if(m_itemView->getAllItems().size() <= 0)
	{
		return;
	}
	int tempFocusIndex = m_selectedItemIndex;
	BaseItem* curItem	= m_itemView->getAllItems().at(m_selectedItemIndex-1);
	Size curItemSize = curItem->getSize();
	Vec2  curItemPos =curItem->getPosition();
	log("Right:The current item position is  (x:%f,y:%f) ----xjx ",curItemPos.x, curItemPos.y);
	int destItemIndex=tempFocusIndex;
	float heightDelta = ITEM_PANEL_SIZE_HEIGHT;
	while(tempFocusIndex < m_itemView->getAllItems().size() )
	{
		tempFocusIndex +=1;
		BaseItem* nextItem = m_itemView->getAllItems().at(tempFocusIndex-1);
		Size nextItemSize = nextItem->getSize();
		Vec2 nextItemPos = nextItem->getPosition();
		log("Right:The next  item position is  (x:%f,y:%f) ----xjx ",nextItemPos.x, nextItemPos.y);
		float minWidthThreshold = nextItemSize.width/2 + curItemSize.width/2;
		float maxWidthThreshold = nextItemSize.width/2 + curItemSize.width/2 + MARGIN_MIDDLE*2;
		if( abs(nextItemPos.x- curItemPos.x) >  minWidthThreshold  &&  abs(nextItemPos.x- curItemPos.x) <  maxWidthThreshold )
		{
			if(abs(nextItemPos.y + nextItemSize.height/2 -  curItemPos.y - curItemSize.height/2) < MARGIN_MIDDLE)
			{
				destItemIndex = tempFocusIndex;
				break;
			}
			if(abs(nextItemPos.y - curItemPos.y) < heightDelta  )
			{
				heightDelta = abs(nextItemPos.y - curItemPos.y) ;
				destItemIndex = tempFocusIndex;
			}
		}
		else if( abs(nextItemPos.x- curItemPos.x)  >  maxWidthThreshold)
		{
			break;
		}
	}
	if(m_selectedItemIndex < destItemIndex)
	{
		m_selectedItemIndex = destItemIndex;
		BaseItem* destItem = m_itemView->getAllItems().at(m_selectedItemIndex-1);
		Size destItemSize = destItem->getSize();
		Vec2 destItemPos=destItem->getPosition();
		m_focusIndicator->setContentSize(Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2));
		m_focusIndicator->setPosition(destItemPos);
		this->onFocusChanged(curItem,destItem);
	}

	if(m_focusIndicator->getPosition().x - m_contentOffsetX > m_itemView->getContentSize().width - 130)
	{
		int scrollOffsetX = 300;
		if( m_itemView->getInnerContainerSize().width - m_contentOffsetX >= m_itemView->getContentSize().width + scrollOffsetX )
		{
			m_contentOffsetX += scrollOffsetX;
			m_itemView->autoScrollPanel(scrollOffsetX,ScrollDirection::Scroll_to_Left);
		}
		else
		{
			scrollOffsetX = m_itemView->getInnerContainerSize().width - m_contentOffsetX -  m_itemView->getContentSize().width;
			log("moed panel width is :%d!------------xjx", scrollOffsetX);
			m_contentOffsetX += scrollOffsetX;
			m_itemView->autoScrollPanel(scrollOffsetX,ScrollDirection::Scroll_to_Left);
		}
	}
	return;
}



void FocusHelper::moveFocusIndicatorToLeft()
{
	if(m_itemView->getAllItems().size() <= 0)
	{
		return;
	}
	int tempFocusIndex = m_selectedItemIndex;
	BaseItem* curItem	= m_itemView->getAllItems().at(tempFocusIndex-1);
	Size curItemSize = curItem->getSize();
	Vec2  curItemPos =curItem->getPosition();
	log("Left:The current item position is  (x:%f,y:%f) ----xjx ",curItemPos.x, curItemPos.y);
	int destItemIndex=tempFocusIndex;
	float heightDelta = ITEM_PANEL_SIZE_HEIGHT;
	while(tempFocusIndex > 1 )
	{
		tempFocusIndex -=1;
		BaseItem* nextItem = m_itemView->getAllItems().at(tempFocusIndex-1);
		Size nextItemSize=nextItem->getContentSize();
		Vec2 nextItemPos = nextItem->getPosition();
		log("Left:The next  item position is  (x:%f,y:%f) ----xjx ",nextItemPos.x, nextItemPos.y);
		float minWidthThreshold = nextItemSize.width/2 + curItemSize.width/2;
		float maxWidthThreshold = nextItemSize.width/2 + curItemSize.width/2 + MARGIN_MIDDLE*2;
		if( abs(nextItemPos.x- curItemPos.x) >  minWidthThreshold  &&  abs(nextItemPos.x- curItemPos.x) <  maxWidthThreshold )
		{
			//......
			if(abs(nextItemPos.y + nextItemSize.height/2 -  curItemPos.y - curItemSize.height/2) < MARGIN_MIDDLE)
			{
				destItemIndex = tempFocusIndex;
				break;
			}
			if(abs(nextItemPos.y - curItemPos.y) < heightDelta  )
			{
				heightDelta = abs(nextItemPos.y - curItemPos.y) ;
				destItemIndex = tempFocusIndex;
			}
		}
		else if( abs(nextItemPos.x- curItemPos.x)  >  maxWidthThreshold)
		{
			break;
		}
	}
	if(m_selectedItemIndex > destItemIndex)
	{
		m_selectedItemIndex = destItemIndex;
		BaseItem* destItem = m_itemView->getAllItems().at(m_selectedItemIndex-1);
		Size destItemSize = destItem->getSize();
		Vec2 destItemPos=destItem->getPosition();
		m_focusIndicator->setContentSize(Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2));
		m_focusIndicator->setPosition(destItemPos);
		this->onFocusChanged(curItem,destItem);
	}
	if(m_focusIndicator->getPosition().x - m_contentOffsetX  < 130)
	{
		int scrollOffsetX = 300;
		if(m_contentOffsetX >= scrollOffsetX)
		{
			m_contentOffsetX -= scrollOffsetX;
			m_itemView->autoScrollPanel(scrollOffsetX,ScrollDirection::Scroll_to_Right);
		}
		else
		{
			scrollOffsetX =  m_contentOffsetX;
			log("moed panel width is :%d!------------xjx", scrollOffsetX);
			m_contentOffsetX = 0;
			m_itemView->autoScrollPanel(scrollOffsetX,ScrollDirection::Scroll_to_Right);
		}
	}
	return;
}

void FocusHelper::moveFocusIndicatorToDown()
{
	if(m_itemView->getAllItems().size() <= 0)
	{
		return;
	}
	int tempFocusIndex = m_selectedItemIndex;
	BaseItem* curItem	= m_itemView->getAllItems().at(tempFocusIndex-1);
	Size curItemSize = curItem->getSize();
	Vec2  curItemPos =curItem->getPosition();
	log("Down:The current item position is  (x:%f,y:%f) ----xjx ",curItemPos.x, curItemPos.y);
	while(tempFocusIndex < m_itemView->getAllItems().size() )
	{
		//...
		tempFocusIndex +=1;
		BaseItem* nextItem = m_itemView->getAllItems().at(tempFocusIndex-1);
		Size nextItemSize = nextItem->getSize();
		Vec2 nextItemPos = nextItem->getPosition();
		log("Down:The next  item position is  (x:%f,y:%f) ----xjx ",nextItemPos.x, nextItemPos.y);
		float heightThreshold =  curItemSize.height/2 + nextItemSize.height/2;
		float widthThrehold =  curItemSize.width/2 + nextItemSize.width/2;
		if(abs(nextItemPos.x - curItemPos.x) > widthThrehold)
		{
			break;
		}
		else if( abs(nextItemPos.y - curItemPos.y) > heightThreshold && abs(nextItemPos.x - curItemPos.x) < MARGIN_MIDDLE)
		{
			m_focusIndicator->setContentSize(Size(nextItemSize.width + m_focusPaddingX*2,nextItemSize.height+m_focusPaddingY*2));
			m_focusIndicator->setPosition(nextItemPos);
			m_selectedItemIndex = tempFocusIndex;
			this->onFocusChanged(curItem,nextItem);
			break;
		}
	}
	return ;
}

void FocusHelper::moveFocusIndicatorToUp()
{
	if(m_itemView->getAllItems().size() <= 0)
	{
		return;
	}
	int tempFocusIndex = m_selectedItemIndex;
	BaseItem* curItem	= m_itemView->getAllItems().at(tempFocusIndex-1);
	Size curItemSize = curItem->getSize();
	Vec2  curItemPos =curItem->getPosition();
	log("Up:The current item position is  (x:%f,y:%f) ----xjx ",curItemPos.x, curItemPos.y);
	while(tempFocusIndex > 1 )
	{
		//...
		tempFocusIndex -=1;
		BaseItem* nextItem = m_itemView->getAllItems().at(tempFocusIndex-1);
		Size nextItemSize=nextItem->getContentSize();
		Vec2 nextItemPos = nextItem->getPosition();
		log("Up:The next  item position is  (x:%f,y:%f) ----xjx ",nextItemPos.x, nextItemPos.y);
		float heightThreshold =  curItemSize.height/2 + nextItemSize.height/2;
		float widthThrehold =  curItemSize.width/2 + nextItemSize.width/2;
		if(abs(nextItemPos.x - curItemPos.x) > widthThrehold)
		{
			break;
		}
		else if( abs(nextItemPos.y - curItemPos.y) > heightThreshold && abs(nextItemPos.x - curItemPos.x) < MARGIN_MIDDLE)
		{
			m_focusIndicator->setContentSize(Size(nextItemSize.width + m_focusPaddingX*2,nextItemSize.height+m_focusPaddingY*2));
			m_focusIndicator->setPosition(nextItemPos);
			m_selectedItemIndex = tempFocusIndex;
			this->onFocusChanged(curItem,nextItem);
			break;
		}
	}
	return;
}

void FocusHelper::showFocusIndicator()
{
	if(m_selectedItemIndex > 0 && !m_focusIndicator->isVisible())
	{
		if(m_selectedItemIndex > m_itemView->getAllItems().size())
		{
			m_selectedItemIndex = m_itemView->getAllItems().size();
		}
		BaseItem* slectedItem	=  m_itemView->getAllItems().at(m_selectedItemIndex-1);
		slectedItem->setFocused(true);
		this->onFocusChanged(nullptr,slectedItem);

		Size itemSize = slectedItem->getSize();
		Vec2  pos =slectedItem->getPosition();
		m_focusIndicator->setContentSize(Size(itemSize.width + m_focusPaddingX*2,itemSize.height +m_focusPaddingY*2));
		m_focusIndicator->setPosition(pos);
		m_focusIndicator->setVisible(true);
	}

	//........................................................Weather to Move the Panel  and Resize the Panel Container
	log("weather to move the panel and resize the panel container!!----------------@xjx");
	BaseItem* lastItem = m_itemView->getAllItems().back();
	Vec2 lastItemPos = lastItem->getPosition();
	Size lastItemSize = lastItem->getSize();
	if(lastItemPos.x + lastItemSize.width/2 - m_contentOffsetX < m_itemView->getContentSize().width -100 )
	{
		int offsetX = m_itemView->getContentSize().width - (lastItemPos.x  + lastItemSize.width/2 - m_contentOffsetX) - 30;
		m_contentOffsetX -= offsetX;
		m_itemView->autoScrollPanel(offsetX,ScrollDirection::Scroll_to_Right);

		Size curInnerContainerSize =  m_itemView->getInnerContainerSize();
		Size newInnerContainerSize = Size(lastItemPos.x +lastItemSize.width/2 + 40,  curInnerContainerSize.height);
		m_itemView->setInnerContainerSize(newInnerContainerSize);
	}
}

void FocusHelper::clearFocusIndicator()
{
	if(m_selectedItemIndex > 0)
	{
		m_focusIndicator->setVisible(false);
		BaseItem* slectedItem	=  m_itemView->getAllItems().at(m_selectedItemIndex-1);
		slectedItem->setFocused(false);
		this->onFocusChanged(slectedItem,NULL);
	}
}


void FocusHelper::onEnterClicked(bool isLongPressed)
{
	m_focusIndicator->setVisible(false);
	m_itemView->onEnterClicked(m_selectedItemIndex,isLongPressed);
	this->showFocusIndicator();
}

void FocusHelper::onFocusChanged(ui::Widget* loseFocusWidget, ui::Widget* getFocusWidget)
{
	if(loseFocusWidget != NULL)
	{
		loseFocusWidget->onFocusChanged(loseFocusWidget,getFocusWidget);
	}
    if(getFocusWidget != NULL)
    {
    	getFocusWidget->onFocusChanged(loseFocusWidget,getFocusWidget);
    }
}


void FocusHelper::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	 switch(keyCode)
	 {
	 case EventKeyboard::KeyCode::KEY_DPAD_CENTER:
		 m_pressedCount +=1;
		 if(m_pressedCount > LONGPRESS_DEFALUTCOUNT && m_pressedFlag)
		 {
			 log("Key with keycode %d is long pressed,pressed count is:%d! ", keyCode, m_pressedCount);
			 m_pressedFlag = false;
			 this ->onEnterClicked(true);
		 }
		 break;
	 case EventKeyboard::KeyCode::KEY_DPAD_RIGHT:
		 this->moveFocusIndicatorToRight();
		 break;
	 case EventKeyboard::KeyCode::KEY_DPAD_LEFT:
		 this->moveFocusIndicatorToLeft();
		 break;
	case EventKeyboard::KeyCode::KEY_DPAD_DOWN:
		 this->moveFocusIndicatorToDown();
		 break;
	case EventKeyboard::KeyCode::KEY_DPAD_UP:
		 this->moveFocusIndicatorToUp();
		 break;
	 default:
			 break;
		 }
	}

void FocusHelper::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
	 switch(keyCode){
	 case EventKeyboard::KeyCode::KEY_DPAD_CENTER:
		 if(m_pressedCount <=  LONGPRESS_DEFALUTCOUNT)
		 {
			 this->onEnterClicked(false);
		 }
		 m_pressedCount = 0;
		 m_pressedFlag = true;
		 break;
	 default:
		 break;
	 }
}




