#include "AppDelegate.h"
#include "HelloWorldScene.h"
#include "UI/MainLayer.h"

USING_NS_CC;
#define DEFALUTSIZE                               CCSizeMake(1280.0f, 720.0f)
AppDelegate::AppDelegate() {

}

AppDelegate::~AppDelegate() 
{
}

bool AppDelegate::applicationDidFinishLaunching() {
    // initialize director
    auto director = Director::getInstance();
    auto glview = director->getOpenGLView();
    if(!glview) {
//        glview = GLView::create("My Game");
    	glview = GLView::create("WeBox Test");
        director->setOpenGLView(glview);
    }

    Size frameSize = glview->getFrameSize();

    float scaleX = (float) frameSize.width / DEFALUTSIZE.width;
    float scaleY = (float) frameSize.height / DEFALUTSIZE.height;

    float scale = 0.0f; // MAX(scaleX, scaleY);
    if (scaleX > scaleY) {
        scale = scaleX / (frameSize.height / (float) DEFALUTSIZE.height);
    } else {
        scale = scaleY / (frameSize.width / (float) DEFALUTSIZE.width);
    }
    if (frameSize.height > 1280.0f && frameSize.width > 720.0f ) {
        CCLog("///// ....1080p");
//        CCFileUtils::sharedFileUtils()->addSearchPath("hd");
        Director::sharedDirector()->setContentScaleFactor(frameSize.width / DEFALUTSIZE.width);
    } else {
        CCLog("///// ....720p");
    }

    CCLog("x: %f; y: %f; scale: %f", scaleX, scaleY, scale);

    glview->setDesignResolutionSize(DEFALUTSIZE.width * scale,
                                                           DEFALUTSIZE.height * scale, kResolutionNoBorder);


    // turn on display FPS
    director->setDisplayStats(true);

    // set FPS. the default value is 1.0/60 if you don't call this
    director->setAnimationInterval(1.0 / 60);

    // create a scene. it's an autorelease object
    auto scene = MainLayer::createScene();

    // run
    director->runWithScene(scene);

    return true;
}

// This function will be called when the app is inactive. When comes a phone call,it's be invoked too
void AppDelegate::applicationDidEnterBackground() {
    Director::getInstance()->stopAnimation();
	JniUtil::getClearnMemoryJNI(false);

    // if you use SimpleAudioEngine, it must be pause
    // SimpleAudioEngine::getInstance()->pauseBackgroundMusic();
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground() {
    Director::getInstance()->startAnimation();

    // if you use SimpleAudioEngine, it must resume here
    // SimpleAudioEngine::getInstance()->resumeBackgroundMusic();
}
