/*
 * ItemData.h
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#ifndef ITEMDATA_H_
#define ITEMDATA_H_

#include "cocos2d.h"

using namespace std;
USING_NS_CC;

class ItemData: public cocos2d::Ref {
public:
    ItemData();
    virtual ~ItemData();
    static ItemData* create();
    virtual bool init();

    void setBackgroundImageFilePath(string backgroundImageFilePath);
    string getBackgroundImageFilePath();

    void setForegroundImageFilePath(string forePath);
    string getForegroundImageFilePath();
    void setBottomBackGroundImageFilePath(string bottomPath);
    string getBottomBackGroundImageFilePath();
    void setHintText(string hintText);
    string getHintText();

    void setAction(string action);
    string getAction();
    void setClass(string cls);
    string getClass();
    void setPackage(string package);
    string getPackage();
    void setParmas(vector<Map<string, Ref*>> parmas);
    vector<Map<string, Ref*>>* getParmas();

    void setClipping(bool clp);
    bool getClipping();
    void setIsShow(bool show);
    bool getIsShow();
    void setID(int ID);
    int getID();
    void setFocusScale(float focusScale);
    float getFocusScale();
    void setGravity(int gravity);
    int getGravity();

    void setWidth(int width);
    int getWidth();
    void setHeight(int height);
    int getHeight();
private:
    string m_backgroundImageFilePath;
    string  m_foregroundImageFilePath;
    string m_bottomBackGroundImageFilePath;
    string m_hintText;


    string m_action;
    string m_class;
    string m_package;

    vector<Map<string, Ref*>> *m_parmas;

    bool m_Clipping;
    bool m_isShow;

    float m_focusScale;
    int m_width;
    int m_height;
    int m_id;
    int m_gravity;

};

#endif /* ITEMDATA_H_ */
