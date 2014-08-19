/*
 * ItemData.h
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#ifndef ITEMDATA_H_
#define ITEMDATA_H_

#include "cocos2d.h"
#include "json/document.h"

using namespace std;
USING_NS_CC;

class ItemData: public cocos2d::Ref {
public:
    ItemData();
    virtual ~ItemData();
    static ItemData* create();
    static ItemData* create( const rapidjson::Value& jsonItem);
    virtual bool init();

    CC_SYNTHESIZE(string,m_backgroundImageFilePath,BackgroundImageFilePath);
    CC_SYNTHESIZE(string,m_foregroundImageFilePath,ForegroundImageFilePath);
    CC_SYNTHESIZE(string,m_bottomBackGroundImageFilePath,BottomBackGroundImageFilePath);
    CC_SYNTHESIZE(string,m_hintText,HintText);
    CC_SYNTHESIZE(string,m_action,Action);
    CC_SYNTHESIZE(string,m_class,Class);
    CC_SYNTHESIZE(string,m_package,Package);

    CC_SYNTHESIZE(string,m_categoryTag,CategoryTag);
    CC_SYNTHESIZE(bool,m_Clipping,Clipping);
    CC_SYNTHESIZE(bool,m_isShow,IsShow);
    CC_SYNTHESIZE(int,m_id,ID);
    CC_SYNTHESIZE(float,m_focusScale,FocusScale);
    CC_SYNTHESIZE(int,m_gravity,Gravity);
    CC_SYNTHESIZE(int,m_width,Width);
    CC_SYNTHESIZE(int,m_height,Height);

    CC_SYNTHESIZE(int,m_proFlag,ProFlag);
    CC_SYNTHESIZE(int,m_code,Code);

    void setParmas(vector<Map<string, Ref*>> parmas);
    vector<Map<string, Ref*>>* getParmas();

private:
    vector<Map<string, Ref*>> *m_parmas;
};

#endif /* ITEMDATA_H_ */
