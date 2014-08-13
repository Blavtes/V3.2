
#ifndef APPITEM_H_
#define APPITEM_H_

#include "PrefixConst.h"
#include "cocos2d.h"
#include "BaseItem.h"
USING_NS_CC;

class AppItem: public BaseItem {
public:
	AppItem();
	virtual ~AppItem();
	CREATE_FUNC(AppItem);
	virtual bool init();

	void setIsUninstalledFlag(bool uninstalledFlag);
	bool getIsUninstalledFlag();
	void setForegroundImage(std::string forgroundImageFilePath);

	void longPressedCallback();
	void onEnterClicked(bool isLongPressed);
	void onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus);
protected:
	ui::ImageView* m_unInstallImage; //showed when the AppItem is ready to  uninstalled
	bool m_isUninstalledFlag; //identify whether the item can be uninstalled
};

#endif /* APPITEM_H_ */
