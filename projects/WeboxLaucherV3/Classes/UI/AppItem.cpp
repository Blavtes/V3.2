
#include "AppItem.h"
USING_NS_CC;

AppItem::AppItem() {
	// ........
	m_itemSize =ITEM_SIZE_APP ;
	m_unInstallImage = nullptr;
	m_isUninstalledFlag = false;
	m_forgroundSprite = nullptr;
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

	m_forgroundSprite = Sprite::create();
	m_forgroundSprite->setOpacityModifyRGB(true);
	m_forgroundSprite->setCascadeOpacityEnabled(true);
	this->addChild(m_forgroundSprite,1);

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

void AppItem::setForegroundSpriteByData(void *data, int w, int h)
{
    Texture2D* textu = new Texture2D();
    textu->initWithData(data, w * h * 4, Texture2D::PixelFormat::RGBA8888, w, h, Size(w, h));
    m_forgroundSprite->setTexture(textu);
    m_forgroundSprite->setTextureRect(Rect(0,0,textu->getContentSize().width,textu->getContentSize().height));
    m_forgroundSprite->setOpacityModifyRGB(true);
    m_forgroundSprite->setBlendFunc(BlendFunc::ALPHA_PREMULTIPLIED);
    m_forgroundSprite->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/3*2));
    textu->release();
}

void AppItem::onEnterClicked(bool isLongPressed)
{
	if(isLongPressed)
	{
		this->longPressedCallback();
	}
	else
	{
		JniUtil::startActivityJNI(m_itemData->getAction().c_str(),m_itemData->getPackage().c_str(),m_itemData->getClass().c_str());
	}
}

void AppItem::longPressedCallback()
{
	log("Enter the AppItem long pressed function! ----------------xjx");
	std::string itemPackageName = "   ";
	log("the item package Name is:%s----xjx",itemPackageName.c_str());
	itemPackageName= m_itemData->getPackage();
	log("the item package Name is:%s----xjx",itemPackageName.c_str());
	if( itemPackageName.compare(APP_SET_NAME)  !=0 && itemPackageName.compare(APP_FILE_NAME) !=0 && itemPackageName.compare(APP_APPSTORE_NAME) !=0)
	{
		log("compared completed!---------------------------xjx");
		if(!m_unInstallImage->isVisible())
		{
			m_unInstallImage->loadTexture(APP_DELETE_IMG);
			m_unInstallImage->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/2));
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




