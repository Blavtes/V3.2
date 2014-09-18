
#include "HelloWorldScene.h"
#include "UI/ItemPanel.h"
#include "UI/BaseItem.h"
#include "UI/AppItem.h"
#include "UI/MainItem.h"
#include "UI/NotificationItem.h"

USING_NS_CC;

static int m_index = 0;
Scene* HelloWorld::createScene( int i)
{
    auto scene = Scene::create();
    auto layer = HelloWorld::create();
    scene->addChild(layer);
    m_index = i ;
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

    ui::ImageView *m_backgroundImageView = ui::ImageView::create();
    m_backgroundImageView->setAnchorPoint(Vec2(0,0));
  	 m_backgroundImageView->setPosition(Vec2::ZERO);
    int rand = random() % 14;
    char ind[100];
    sprintf(ind,"bg/%d.jpg",rand);
    log(" path ------------> %s",ind);
    m_backgroundImageView->loadTexture(ind);
  	 this->addChild(m_backgroundImageView,0);


   auto listener = EventListenerKeyboard::create();
   listener->onKeyPressed = CC_CALLBACK_2(HelloWorld::onKeyPressed,this);
   listener->onKeyReleased = CC_CALLBACK_2(HelloWorld::onKeyReleased, this);
//    _eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);
//    Director::getInstance()->getEventDispatcher()->setEnabled(true);
    	Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener,this);
    	this->scheduleOnce(schedule_selector(HelloWorld::replaceScene),10);
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

void HelloWorld::beginAction()
{
    if (m_index < 0) {
        m_index = 100;
    }
    int rand = m_index % 35;
    log("////////// index %d",rand);
    switch (rand) {
        case 0:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSun::create();
            mit->setPosition(Vec2(600, 360));
            mit->setEndSize(5);
            mit->setEndSizeVar(5);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionCrossFade::create(4, hell));
        }
            break;
        case 1:
        {
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleFireworks::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(30);
            //            mit->setStartSizeVar(5);
            //            mit->setEndSize(10);
            //            mit->setEndSizeVar(2);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionCrossFade::create(4, hell));
        }
            break;
        case 2:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleFire::create();
            mit->setPosition(Vec2(600, 360));
            mit->setPosVar(Vec2(20,0));
            mit->setStartSize(30);
            mit->setStartSizeVar(5);
            mit->setEndSize(10);
            mit->setEndSizeVar(2);
            hell->addChild(mit);
            Director::getInstance()->replaceScene(TransitionFade::create(4, hell));
        }
            break;
        case 3:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSun::create();

            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(30);
            //            mit->setStartSizeVar(5);
            mit->setEndSize(10);
            mit->setEndSizeVar(2);

            Director::getInstance()->replaceScene(TransitionFadeBL::create(4, hell));
        }
            break;
        case 4:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleGalaxy::create();
            mit->setPosition(Vec2(600, 360));
            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(30);
            //            mit->setStartSizeVar(5);
            mit->setEndSize(10);
            mit->setEndSizeVar(2);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionFadeDown::create(4, hell));
        }
            break;
        case 5:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleFlower::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(30);
            //            mit->setStartSizeVar(5);
            mit->setEndSize(10);
            mit->setEndSizeVar(2);
            hell->addChild(mit);
            Director::getInstance()->replaceScene(TransitionFadeTR::create(4, hell));

        }
            break;
        case 6:{

            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleMeteor::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            mit->setStartSize(10);
            mit->setStartSizeVar(2);
            mit->setEndSize(2);
            mit->setEndSizeVar(1);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionFadeUp::create(4, hell));

        }

            break;
        case 7:{

            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSpiral::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(10);
            //            mit->setStartSizeVar(2);
            //            mit->setEndSize(2);
            //            mit->setEndSizeVar(1);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionFlipAngular::create(4, hell));
        }
            break;
        case 8:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleExplosion::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(10);
            //            mit->setStartSizeVar(2);
            mit->setEndSize(5);
            mit->setEndSizeVar(5);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionFlipX::create(4, hell));
        }
            break;
        case 9:{
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSmoke::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(10);
            //            mit->setStartSizeVar(2);
            mit->setEndSize(5);
            mit->setEndSizeVar(5);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionFlipY::create(4, hell));
        }
            break;
        case 10:
        {
            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSnow::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(10);
            //            mit->setStartSizeVar(2);
            //            mit->setEndSize(5);
            //            mit->setEndSizeVar(5);
            hell->addChild(mit);
            Director::getInstance()->replaceScene(TransitionJumpZoom::create(4, hell));
        }
            break;
        case 11:{

            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleSnow::create();
            mit->setPosition(Vec2(600, 360));
            //            mit->setPosVar(Vec2(20,0));
            //            mit->setStartSize(10);
            //            mit->setStartSizeVar(2);
            //            mit->setEndSize(5);
            //            mit->setEndSizeVar(5);
            hell->addChild(mit);

            Director::getInstance()->replaceScene(TransitionMoveInB::create(4, hell));
        }
            break;
        case 12: {

            Scene *hell = HelloWorld::createScene(m_index);
            auto mit = ParticleRain::create();
            mit->setPosition(Vec2(600, 360));
            mit->setPosVar(Vec2(20,0));
            mit->setStartSize(50);
            mit->setStartSizeVar(10);
            mit->setEndSize(10);
            mit->setEndSizeVar(5);
            hell->addChild(mit);
            Director::getInstance()->replaceScene(TransitionMoveInL::create(4, hell));
        }
            break;
        case 13:
            Director::getInstance()->replaceScene(TransitionMoveInR::create(4, HelloWorld::createScene(m_index)));
            break;
        case 14:
            Director::getInstance()->replaceScene(TransitionMoveInT::create(4, HelloWorld::createScene(m_index)));
            break;
        case 15:
            Director::getInstance()->replaceScene(TransitionSplitCols::create(4, HelloWorld::createScene(m_index)));
            break;
        case 16:
            Director::getInstance()->replaceScene(TransitionSplitRows::create(4, HelloWorld::createScene(m_index)));
            break;
        case 17:
            Director::getInstance()->replaceScene(TransitionPageTurn::create(4, HelloWorld::createScene(m_index), false));
            break;
        case 18:
            Director::getInstance()->replaceScene(TransitionProgressInOut::create(4, HelloWorld::createScene(m_index)));
            break;
        case 19:
            Director::getInstance()->replaceScene(TransitionProgressOutIn::create(4, HelloWorld::createScene(m_index)));
            break;
        case 20:
            Director::getInstance()->replaceScene(TransitionProgressRadialCCW::create(4, HelloWorld::createScene(m_index)));
            break;
        case 21:
            Director::getInstance()->replaceScene(TransitionProgressRadialCW::create(4, HelloWorld::createScene(m_index)));
            break;
        case 22:
            Director::getInstance()->replaceScene(TransitionProgressVertical::create(4, HelloWorld::createScene(m_index)));
            break;
        case 23:
//            Director::getInstance()->replaceScene(TransitionSceneOriented::create(4, HelloWorld::createScene(m_index), cocos2d::TransitionScene::Orientation::DOWN_OVER));
//            break;
        case 24:
            Director::getInstance()->replaceScene(TransitionShrinkGrow::create(4, HelloWorld::createScene(m_index)));
            break;
        case 25:
            Director::getInstance()->replaceScene(TransitionSlideInB::create(4, HelloWorld::createScene(m_index)));
            break;
        case 26:
            Director::getInstance()->replaceScene(TransitionSlideInL::create(4, HelloWorld::createScene(m_index)));
            break;
        case 27:
            Director::getInstance()->replaceScene(TransitionSlideInR::create(4, HelloWorld::createScene(m_index)));
            break;
        case 28:
            Director::getInstance()->replaceScene(TransitionSlideInT::create(4, HelloWorld::createScene(m_index)));
            break;
        case 29:
            Director::getInstance()->replaceScene(TransitionSplitCols::create(4, HelloWorld::createScene(m_index)));
            break;
        case 30:
            Director::getInstance()->replaceScene(TransitionSplitRows::create(4, HelloWorld::createScene(m_index)));
            break;
        case 31:
            Director::getInstance()->replaceScene(TransitionTurnOffTiles::create(4, HelloWorld::createScene(m_index)));
            break;
        case 32:
            Director::getInstance()->replaceScene(TransitionZoomFlipAngular::create(4, HelloWorld::createScene(m_index), cocos2d::TransitionScene::Orientation::DOWN_OVER));
            break;
        case 33:
            Director::getInstance()->replaceScene(TransitionZoomFlipX::create(4, HelloWorld::createScene(m_index), cocos2d::TransitionScene::Orientation::LEFT_OVER));
            break;
        case 34:
            Director::getInstance()->replaceScene(TransitionZoomFlipY::create(4, HelloWorld::createScene(m_index), cocos2d::TransitionScene::Orientation::UP_OVER));
        default:
            break;
    }

}

void HelloWorld::replaceScene(float dt)
{
    m_index++;

    beginAction();

}

void HelloWorld::onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event)
{
    log("Key with keycode %d released  -------xjx", keyCode);
    this->unschedule(schedule_selector(HelloWorld::replaceScene));

    if (keyCode == EventKeyboard::KeyCode::KEY_BACK) {
        Director::getInstance()->popScene();
        return;
    }

    if (keyCode == EventKeyboard::KeyCode::KEY_DPAD_LEFT) {
        m_index--;
            beginAction();
    } else if (keyCode == EventKeyboard::KeyCode::KEY_DPAD_RIGHT) {
        m_index++;
        beginAction();
    } else {
        Director::getInstance()->popScene();
    }

    return;

}

void HelloWorld::menuCloseCallback(Ref* pSender)
{
//#if (CC_TARGET_PLATFORM == CC_PLATFORM_WP8) || (CC_TARGET_PLATFORM == CC_PLATFORM_WINRT)
//	MessageBox("You pressed the close button. Windows Store Apps do not implement a close button.","Alert");
//    return;
//#endif
//
//    Director::getInstance()->end();
//
//#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
//    exit(0);
//#endif
}



