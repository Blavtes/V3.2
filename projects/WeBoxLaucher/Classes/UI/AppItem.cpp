
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
	this->setFocusEnabled(true);
	this->onFocusChanged = CC_CALLBACK_2(AppItem::onFocusChange, this);

	m_unInstallImage = ui::ImageView::create();
	m_unInstallImage->setPosition(Vec2::ZERO);
	m_unInstallImage->setVisible(false);
	this->addChild(m_unInstallImage,2);

	return true;
}

void AppItem::setBackgroundImage(std::string backgroundImageFilePath, Size contentSize)
{
	if(!backgroundImageFilePath.empty())
	{
		m_backgroundImage->loadTexture(backgroundImageFilePath);
		this->setPositionAndSize(m_backgroundImage,contentSize,false);
	}
}

void AppItem::setForegroundImage(std::string foregroundImageFilePath,Size contentSize)
{
	if(!foregroundImageFilePath.empty())
	{
		m_foregroundImage->loadTexture(foregroundImageFilePath);
		m_foregroundImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/3*2));
	}
}


void AppItem::setHintText(std::string hintText)
{
	if(!hintText.empty())
	{
		m_hintText->setString(hintText);
		m_hintText->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/6));
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
			this->setPositionAndSize(m_unInstallImage,ITEM_SIZE_APP,false);
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




