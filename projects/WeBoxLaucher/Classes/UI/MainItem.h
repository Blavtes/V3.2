
#ifndef MAINITEM_H_
#define MAINITEM_H_



#include "../extensions/cocos-ext.h"
#include "cocos2d.h"
#include "PrefixConst.h"
#include "BaseItem.h"

USING_NS_CC;
USING_NS_CC_EXT;

class MainItem: public BaseItem {
public:
	MainItem();
	virtual ~MainItem();
	CREATE_FUNC(MainItem);
	virtual bool init();

	void updateSize();
	void setBottomBackgroundImage(std::string bottomBgImageFilePath, Size bottomContentSize=BOTTOM_PART_SIZE_MAIN);
	void setForegroundImage(std::string forgroundImageFilePath, Size contentSize = TOP_PART_SIZE_MAIN);
	void setBackgroundImage(std::string backgroundImageFilePath, Size contentSize = TOP_PART_SIZE_MAIN);
    void setHintText(std::string hintText);
	void setPositionAndSize(ui::Widget* widget, Size contentSize,bool isTopPart = true);

    void onEnterClicked(bool isLongPressed);
	void onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus);
protected:
	 ui::ImageView* m_bottomBackgroundImage;
	 Size m_bottomSize;
	 Size m_topSize;
};

#endif /* APPITEM_H_ */
