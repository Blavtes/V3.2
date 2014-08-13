
#ifndef MAINITEM_H_
#define MAINITEM_H_

#include "PrefixConst.h"
#include "cocos2d.h"
#include "BaseItem.h"
USING_NS_CC;

class MainItem: public BaseItem {
public:
	MainItem();
	virtual ~MainItem();
	CREATE_FUNC(MainItem);
	virtual bool init();

	virtual void setItemData(ItemData* itemData);
	void setBottomBackgroundImage(std::string bottomBgImageFilePath);

    void onEnterClicked(bool isLongPressed);
	void onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus);
protected:
	 ui::ImageView* m_bottomBackgroundImage;
};

#endif /* APPITEM_H_ */
