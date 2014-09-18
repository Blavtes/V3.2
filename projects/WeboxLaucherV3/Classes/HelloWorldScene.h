


#ifndef __HELLOWORLD_SCENE_H__
#define __HELLOWORLD_SCENE_H__
#include  "UI/PrefixConst.h"
#include "cocos2d.h"
#include "../extensions/cocos-ext.h"
#include "UI/FocusHelper.h"
USING_NS_CC;
USING_NS_CC_EXT;
class HelloWorld : public cocos2d::Layer
{
public:
    // there's no 'id' in cpp, so we recommend returning the class instance pointer
    static cocos2d::Scene* createScene(int i);

    // Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
    virtual bool init();  
    
    // a selector callback
    void menuCloseCallback(cocos2d::Ref* pSender);
    
    // implement the "static create()" method manually
    CREATE_FUNC(HelloWorld);
    
    void onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event);
    void onKeyReleased(EventKeyboard::KeyCode keyCode, Event* event);
    void onFocusChange(ui::Widget *focus1,ui::Widget *foucs2);
    void replaceScene(float dt);
    void beginAction();
protected:
    ui::ScrollView* m_scrollView;
    FocusHelper* m_focusHelper;
};

#endif // __HELLOWORLD_SCENE_H__
