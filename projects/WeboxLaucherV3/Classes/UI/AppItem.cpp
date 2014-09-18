
#include "AppItem.h"
USING_NS_CC;

AppItem::AppItem() {
	// ........
	m_itemSize =ITEM_SIZE_APP ;
	m_unInstallImage = nullptr;
	m_isUninstalledFlag = false;
	m_forgroundSprite = nullptr;
	m_titlePanel = nullptr;
	m_titleString = nullptr;
	m_showString = nullptr;
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
    float scale = 1.0f;
    Size frameSize = Director::getInstance()->getOpenGLView()->getFrameSize();
    if (frameSize.height > 720) {
        scale = frameSize.height / 720;
    }

    Texture2D* textu = new Texture2D();
    textu->initWithData(data, w * h * 4, Texture2D::PixelFormat::RGBA8888, w, h, Size(w, h));
    m_forgroundSprite->setTexture(textu);
    m_forgroundSprite->setTextureRect(Rect(0,0,textu->getContentSize().width,textu->getContentSize().height));
    m_forgroundSprite->setOpacityModifyRGB(true);
    m_forgroundSprite->setBlendFunc(BlendFunc::ALPHA_PREMULTIPLIED);
    m_forgroundSprite->setPosition(Vec2(m_itemSize.width/2,m_itemSize.height/3*2));

    m_forgroundSprite->setScale(1 / scale, 1 /scale);

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
	 if(widgetLoseFocus != NULL && this == widgetLoseFocus )
	 {
		AppItem* appItemLoseFocus = dynamic_cast<AppItem*>(widgetLoseFocus);
		if(appItemLoseFocus != NULL)
		{
			if(m_titlePanel != nullptr && m_titleString != nullptr)
			{
				m_titleString->stopAllActions();
				m_titleString->setVisible(false);
				m_titleString->setPosition(Vec2(m_titlePanel->getContentSize().width/2+40,m_titlePanel->getContentSize().height/2));
				m_showString->setVisible(true);
//				m_titleString->setPosition(Vec2(m_titlePanel->getContentSize().width/2,m_titlePanel->getContentSize().height/2));
			}
			if(m_isUninstalledFlag)
			{
				appItemLoseFocus->m_unInstallImage->setVisible(false);
				appItemLoseFocus->setIsUninstalledFlag(false);
			}
		}
		widgetLoseFocus->setFocused(false);
	 }



	 if(widgetGetFocus != NULL &&  this == widgetGetFocus)
	 {
		AppItem* appItemgetFocus = dynamic_cast<AppItem*>(widgetGetFocus);
		if(appItemgetFocus != NULL && m_titlePanel != nullptr && m_titleString != nullptr)
		{
			m_showString->setVisible(false);
			m_titleString->setVisible(true);
			if(m_titleString->getContentSize().width > m_itemSize.width)
			{
				MoveTo*  textMove1 = MoveTo::create(3,Vec2(-m_titleString->getContentSize().width/2,m_titleString->getPosition().y));
				Place* textPlace = Place::create(Vec2(m_itemSize.width + m_titleString->getContentSize().width/2,m_titleString->getPosition().y));
				MoveTo*  textMove2 = MoveTo::create(3,Vec2(m_titlePanel->getContentSize().width/2,m_titleString->getPosition().y));
				Sequence* testAction = Sequence::create(textMove1,textPlace,textMove2,nullptr);
				RepeatForever* repeatAction = RepeatForever::create(testAction);
				m_titleString->runAction(repeatAction);
			}
		}
		 widgetGetFocus->setFocused(true);

	 }
}


void AppItem::setIsUninstalledFlag(bool uninstalledFlag)
{
	m_isUninstalledFlag = uninstalledFlag;
	if(!uninstalledFlag)
	{
		m_unInstallImage->setVisible(false);
	}
}

bool AppItem::getIsUninstalledFlag()
{
	return m_isUninstalledFlag;
}


 void AppItem::setPartDisplayText(std::string hintText)
{
	log("The title is wider than the Item width, Part display the title!===========@title");
	if(m_titlePanel == nullptr)
	{
		m_titlePanel = ui::Layout::create();
		m_titlePanel->setContentSize(Size(240,40));
		m_titlePanel->setAnchorPoint(Vec2(0.5,0.5));
		m_titlePanel->setPosition(Vec2(m_itemSize.width/2,35));
		m_titlePanel->ignoreContentAdaptWithSize(true);
		m_titlePanel->setClippingEnabled(true);
		this->addChild(m_titlePanel);
	}
	if(m_titleString == nullptr)
	{
		m_titleString = ui::Text::create("","Arial",28);
		m_titleString->setPosition(Vec2(m_titlePanel->getContentSize().width/2+40,m_titlePanel->getContentSize().height/2));
	}
	m_titleString->setString(hintText);
	m_titleString->setVisible(false);
	m_titlePanel->addChild(m_titleString);

	if(m_showString == nullptr)
	{
		m_showString = ui::Text::create("","Arial",28);
		m_showString->setPosition(Vec2(m_titlePanel->getContentSize().width/2,m_titlePanel->getContentSize().height/2));
	}
	std::string  str = hintText.substr(0,18)+"...";
	m_showString->setString(str);
	m_titlePanel->addChild(m_showString);
}




