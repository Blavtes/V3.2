
#include "HelloWorldScene.h"
#include "UI/ItemPanel.h"
#include "UI/BaseItem.h"
#include "UI/AppItem.h"
#include "UI/MainItem.h"
#include "UI/NotificationItem.h"

USING_NS_CC;

Scene* HelloWorld::createScene()
{
    auto scene = Scene::create();
    auto layer = HelloWorld::create();
    scene->addChild(layer);
    return scene;
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Layer::init() )
    {
        return false;
    }
//    visibleSize = Director::getInstance()->getVisibleSize();
//    Vec2 origin = Director::getInstance()->getVisibleOrigin();

//	Size visibleSize=CCDirector::getInstance()->getVisibleSize();
//    ui::ImageView* backgroundImageView = ui::ImageView::create();
//    backgroundImageView->loadTexture("background.jpg");
//    backgroundImageView->setPosition(Vec2(visibleSize.width/2,visibleSize.height/2));
//    this->addChild(backgroundImageView);

//    auto itemPanel = ItemPanel::create();
//    itemPanel->setPosition(Vec2(10,100));
//    this->addChild(itemPanel,1);
//
//    m_focusHelper = FocusHelper::create();
//    m_focusHelper->bindItemPanel(itemPanel);
//    m_focusHelper->retain();//very important, otherwise , it is autoreleased in the first frame refresh.


   auto listener = EventListenerKeyboard::create();
   listener->onKeyPressed = CC_CALLBACK_2(HelloWorld::onKeyPressed,this);
   listener->onKeyReleased = CC_CALLBACK_2(HelloWorld::onKeyReleased, this);
    _eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);
    return true;
}


void HelloWorld::onFocusChange(cocos2d::ui::Widget *widgetLostFocus, cocos2d::ui::Widget *widgetGetFocus)
{

}
// 键位响应函数原型
void HelloWorld::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
    log("Key with keycode %d pressed ", keyCode);
//    m_focusHelper->onKeyPressed(keyCode,event);


}

void HelloWorld::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
    log("Key with keycode %d released  -------xjx", keyCode);
//    m_focusHelper->onKeyReleased(keyCode,event);
//    auto layout = dynamic_cast<ui::VBox*>(this->getChildByTag(10));
//    auto fouch2 = dynamic_cast<ui::Text*>(layout->getChildByTag(11));
//    auto fouch1 = dynamic_cast<ui::Text*>(layout->getChildByTag(12));
//    fouch1->setFocused(!fouch1->isFocused());
//    fouch2->setFocused(!fouch2->isFocused());

//    if (keyType == EventKeyboard::KeypadEventType::kTypeDown) {
//        fouch1->setFocused(true);
////        fouch2->setFocused(false);
//    } else if (keyType == EventKeyboard::KeypadEventType::kTypeUp){
//        fouch2->setFocused(true);
////        fouch1->setFocused(false);
//    }
//    log("end");

}

void HelloWorld::menuCloseCallback(Ref* pSender)
{
#if (CC_TARGET_PLATFORM == CC_PLATFORM_WP8) || (CC_TARGET_PLATFORM == CC_PLATFORM_WINRT)
	MessageBox("You pressed the close button. Windows Store Apps do not implement a close button.","Alert");
    return;
#endif

    Director::getInstance()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
}



