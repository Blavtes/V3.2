
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

	void setBottomBackgroundImage(std::string bottomBgImageFilePath);

    void onEnterClicked(bool isLongPressed);
	void onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus);
protected:
	 ui::ImageView* m_bottomBackgroundImage;
};

#endif /* APPITEM_H_ */
