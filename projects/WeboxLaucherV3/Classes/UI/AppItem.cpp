
#include "AppItem.h"
USING_NS_CC;
USING_NS_CC_EXT;

AppItem::AppItem() {
	// ........
	m_itemSize =ITEM_SIZE_APP ;
	m_unInstallImage = NULL;
	m_isUninstalledFlag = false;
}

AppItem::~AppItem() {
	// ......
}

bool AppItem::init()
{
	if( !BaseItem::init() )
	{
		return false;
	}
	this->setContentSize(m_itemSize);
	this->setFocusEnabled(true);
	this->onFocusChanged = CC_CALLBACK_2(AppItem::onFocusChange, this);

	m_unInstallImage = ui::ImageView::create();
	m_unInstallImage->setPosition(Vec2::ZERO);
	m_unInstallImage->setVisible(false);
	this->addChild(m_unInstallImage,2);

	return true;
}


void AppItem::setForegroundImage(std::string foregroundImageFilePath)
{
	if(!foregroundImageFilePath.empty())
	{
		m_foregroundImage->loadTexture(foregroundImageFilePath);
		m_foregroundImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/3*2));
	}
}


void AppItem::onEnterClicked(bool isLongPressed)
{
	if(isLongPressed)
	{
		this->longPressedCallback();
	}
	else
	{
		log("Call The AppItem!------------------------xjx");
	}
}

void AppItem::longPressedCallback()
{
	log("Enter the AppItem long pressed function! ----------------xjx");
	if( m_itemName.compare(APP_SET_NAME)  !=0 && m_itemName.compare(APP_FILE_NAME) !=0 )
	{
		if(!m_unInstallImage->isVisible())
		{
			m_unInstallImage->loadTexture(APP_DELETE_IMG);
			m_unInstallImage->setPosition(Vec2(ITEM_SIZE_APP.width/2,ITEM_SIZE_APP.height/2));
			m_unInstallImage->setVisible(true);
			this->setIsUninstalledFlag(true);
		}
	}
	else
	{
		this->onEnterClicked(false);
	}
}

void AppItem::onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus)
{
	log("the AppItem function onFocusChange() is called automatically!--------------------------xjx");
	 if(widgetLoseFocus != NULL)
	 {
		AppItem* appItemLoseFocus = dynamic_cast<AppItem*>(widgetLoseFocus);
		if(appItemLoseFocus != NULL)
		{
			appItemLoseFocus->m_unInstallImage->setVisible(false);
			appItemLoseFocus->setIsUninstalledFlag(false);
		}
		widgetLoseFocus->setFocused(false);
	 }
	 if(widgetGetFocus != NULL)
	 {
		 widgetGetFocus->setFocused(true);
	 }
}


void AppItem::setIsUninstalledFlag(bool uninstalledFlag)
{
	m_isUninstalledFlag = uninstalledFlag;
}

bool AppItem::getIsUninstalledFlag()
{
	return m_isUninstalledFlag;
}



