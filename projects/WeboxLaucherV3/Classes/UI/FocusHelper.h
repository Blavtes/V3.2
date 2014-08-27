/*
 * FocusHelper.h
 *
 *  Created on: 2014年7月28日
 *      Author: xjx
 */

#ifndef FOCUSHELPER_H_
#define FOCUSHELPER_H_

#include "cocos2d.h"
#include "../extensions/cocos-ext.h"
#include "ItemPanel.h"
USING_NS_CC;
USING_NS_CC_EXT;

class FocusHelper : public Ref {
public:
	FocusHelper();
	virtual ~FocusHelper();
	static FocusHelper* create();
	virtual bool init();
	void bindItemPanel(ItemPanel* itemPanel,std::string focusIndicatorImageFilePath = DEFAULT_FOCUS_INDICATOR_IMG);
	void initFocusIndicator(std::string focusIndicatorImageFilePath = DEFAULT_FOCUS_INDICATOR_IMG);

	void moveFocusIndicatorToRight();
	void moveFocusIndicatorToLeft();
	void moveFocusIndicatorToDown();
	void moveFocusIndicatorToUp();
	void adjustIndicatorAndPanelPosition(Vec2);
	void adjustIndicatorSize();
	void updateItemView();
	void showFocusIndicator();
	void clearFocusIndicator();
	void updateIndicatorSize(float dt);

    void onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event);
    void onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event);
    void onEnterClicked(bool isLongPressed);
    void onFocusChanged(ui::Widget* loseFocusWidget, ui::Widget* getFocusWidget);
    int getSelectedItemIndex();

protected:
	ItemPanel* m_itemView;
	Scale9Sprite* m_focusIndicator; //-----the follow 4 var used for focusIndicator
	int m_selectedItemIndex;
	int m_focusPaddingX;
	int m_focusPaddingY;
	int m_contentOffsetX;//used for move panel
	int m_pressedCount;//used for long pressed
	bool m_pressedFlag;
	Size m_deltaSize;


};

#endif /* FOCUSHELPER_H_ */
