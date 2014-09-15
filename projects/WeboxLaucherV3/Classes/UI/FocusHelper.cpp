/*
 * FocusHelper.cpp
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#include "FocusHelper.h"
#include "Utils/SizeTo.h"

FocusHelper::FocusHelper()
{
	m_itemView = nullptr;
	m_focusIndicator = nullptr;
	m_focusPaddingX = 26;
	m_focusPaddingY = 26;
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
		if(m_itemView->getMainItemCount() > 0 && m_itemView->getAllItems().size() > 0 )
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

void  FocusHelper::setSelectedItemIndex(int selectedIndex)
{
	m_selectedItemIndex = selectedIndex;
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
//		m_focusIndicator->setContentSize(Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2));
		Size indicatorSize =  Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2);
		this->adjustIndicatorAndPanelPosition(destItemPos,indicatorSize);
		this->onFocusChanged(curItem,destItem);
	}
	return;
}

void FocusHelper::adjustIndicatorAndPanelPosition(Vec2 pos, Size indicatorSize)
{
	Size visibleSize = Director::getInstance()->getVisibleSize();
	m_focusIndicator->stopAllActions();  //-------------------------stop former Action, otherwise the new Action will be ignored!
	m_itemView->getInnerContainer()->stopAllActions();
	Vec2 indicatorPos = m_focusIndicator->getPosition();
	Vec2 panelPos = m_itemView->getInnerContainer()->getPosition();
	Vec2 nextIndicatorPos = panelPos+pos;
	SizeTo* sizeAdjust = SizeTo::create(ACTION_DURATION_TIME,indicatorSize.width,indicatorSize.height);
    if(nextIndicatorPos.x >= visibleSize.width - 170)
    {
        MoveTo* indicatorMove = MoveTo::create(ACTION_DURATION_TIME,pos);
        m_focusIndicator->runAction(Spawn::createWithTwoActions(sizeAdjust,indicatorMove));

		MoveTo* panelMove = MoveTo::create(1,Vec2(visibleSize.width - 170-pos.x,0));
		EaseExponentialOut* sineActionPanel= EaseExponentialOut::create(panelMove);
		m_itemView->getInnerContainer()->runAction(sineActionPanel);
    }
    else if(nextIndicatorPos.x <= 170)
    {
        MoveTo* indicatorMove = MoveTo::create(ACTION_DURATION_TIME,pos);
        m_focusIndicator->runAction(Spawn::createWithTwoActions(sizeAdjust,indicatorMove));

       if(panelPos.x != 0 )
       {
			MoveTo* panelMove = MoveTo::create(ACTION_DURATION_TIME,Vec2(  - pos.x+170,0));
			EaseExponentialOut* sineActionPanel= EaseExponentialOut::create(panelMove);
			sineActionPanel->setDuration(1);
			m_itemView->getInnerContainer()->runAction(sineActionPanel);
       }
    }
    else
    {
        MoveTo* indicatorMove = MoveTo::create(ACTION_DURATION_TIME,pos);
        m_focusIndicator->runAction(Spawn::createWithTwoActions(sizeAdjust,indicatorMove));
    }
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
//		m_focusIndicator->setContentSize(Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2));
		Size indicatorSize =  Size(destItemSize.width + m_focusPaddingX*2,destItemSize.height+m_focusPaddingY*2);
		this->adjustIndicatorAndPanelPosition(destItemPos,indicatorSize);
		this->onFocusChanged(curItem,destItem);
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
    bool isFind = false;
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
			m_focusIndicator->stopAllActions();
			m_focusIndicator->setContentSize(Size(nextItemSize.width + m_focusPaddingX*2,nextItemSize.height+m_focusPaddingY*2));
	     	MoveTo* focusindicatorMove = MoveTo::create(0.06,nextItemPos);
	     	m_focusIndicator->runAction(focusindicatorMove);
			m_selectedItemIndex = tempFocusIndex;
			this->onFocusChanged(curItem,nextItem);
      isFind = true;
			break;
		}
	}
    BaseItem* preItem = m_itemView->getAllItems().at(tempFocusIndex-2);
    Size preItemSize = preItem->getSize();
    Vec2 preItemPos = preItem->getPosition();
    float heightThreshold =  curItemSize.height/2 + preItemSize.height/2;

    if (isFind) {
        return;
    }
    if (tempFocusIndex == m_itemView->getAllItems().size() && curItemPos.y > heightThreshold) {
        BaseItem* chItem = m_itemView->getAllItems().at(tempFocusIndex-2);
        Size chItemSize = chItem->getSize();
        Vec2 chItemPos = chItem->getPosition();
        MoveTo* focusindicatorMove = MoveTo::create(ACTION_DURATION_TIME,chItemPos);
        m_focusIndicator->runAction(focusindicatorMove);
        m_selectedItemIndex = tempFocusIndex - 1;
        this->onFocusChanged(curItem,chItem);
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
			m_focusIndicator->stopAllActions();
			m_focusIndicator->setContentSize(Size(nextItemSize.width + m_focusPaddingX*2,nextItemSize.height+m_focusPaddingY*2));
//			m_focusIndicator->setPosition(nextItemPos);
	     	MoveTo* focusindicatorMove = MoveTo::create(0.06,nextItemPos);
	     	m_focusIndicator->runAction(focusindicatorMove);
			m_selectedItemIndex = tempFocusIndex;
			this->onFocusChanged(curItem,nextItem);
			break;
		}
	}
	return;
}

void FocusHelper::showFocusIndicator()
{
	int itemCount = m_itemView->getAllItems().size();
	if(itemCount == 0)
	{
		log("FocusHelper::the items is o-----------------------------------@clear!!!");
		m_selectedItemIndex = 0;
		m_focusIndicator->setVisible(false);
		return;
	}
	if(m_selectedItemIndex > itemCount ||   !m_focusIndicator->isVisible())
	{
		//
		if(m_selectedItemIndex > itemCount)
		{
			m_selectedItemIndex = m_itemView->getAllItems().size();
			//-------get the former size, and calculate the m_deltaSize
		}
		BaseItem* slectedItem	=  m_itemView->getAllItems().at(m_selectedItemIndex-1);
		slectedItem->setFocused(true);
		this->onFocusChanged(nullptr,slectedItem);

		Size itemSize = slectedItem->getSize();
		Vec2  pos =slectedItem->getPosition();
//		m_focusIndicator->setContentSize(Size(itemSize.width + m_focusPaddingX*2,itemSize.height +m_focusPaddingY*2));
		Size indicatorSize = Size(itemSize.width + m_focusPaddingX*2,itemSize.height +m_focusPaddingY*2);
		this->adjustIndicatorAndPanelPosition(pos,indicatorSize);
		log("FocusHelper::showFocusIndicator-222222----------------------------------@clear!!!");
		m_focusIndicator->setVisible(true);
	}

	this->updateItemView();
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

void FocusHelper::updateItemView()
{
	//	........................................................Weather to Move the Panel  and Resize the Panel Container After Item Deleted
	Size visibleSize = Director::getInstance()->getVisibleSize();
	BaseItem* lastItem = m_itemView->getAllItems().back();
	Vec2 lastItemPos = lastItem->getPosition();
	Size lastItemSize = lastItem->getSize();
	Vec2 panelPos = m_itemView->getInnerContainer()->getPosition();
	if(lastItemPos.x + panelPos.x < m_itemView->getContentSize().width -170)
	{
		Size curInnerContainerSize =  m_itemView->getInnerContainerSize();
		Size newInnerContainerSize = Size(lastItemPos.x +lastItemSize.width/2 + 40,  curInnerContainerSize.height);
		m_itemView->setInnerContainerSize(newInnerContainerSize);
	}
}

void FocusHelper::onEnterClicked(bool isLongPressed)
{
	if(m_focusIndicator == nullptr || m_selectedItemIndex == 0)
	{
		return;
	}
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
	 switch(keyCode)
	 {
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




