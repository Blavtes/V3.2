/*
 * ItemData.cpp
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#include "ItemData.h"

using namespace std;

ItemData::ItemData()
: m_focusScale(1.0)
, m_gravity(-1)
, m_package("")
, m_class("")
, m_action("")
, m_backgroundImageFilePath("")
, m_foregroundImageFilePath("")
, m_bottomBackGroundImageFilePath("")
, m_hintText("")
, m_Clipping(false)
, m_isShow(true)
, m_id(-1)
, m_height(0)
, m_width(0)
{
    m_parmas = new vector<Map<string, Ref*>>();
}

ItemData::~ItemData() {
    delete m_parmas;
}

ItemData* ItemData::create()
{
    ItemData* itemData = new ItemData();
    if(itemData && itemData->init()){
        itemData->autorelease();
        return itemData;
    }
    CC_SAFE_DELETE(itemData);
    return NULL;
}

bool ItemData::init()
{
    return true;
}

void ItemData::setBackgroundImageFilePath(string backgroundImageFilePath)
{
    m_backgroundImageFilePath = backgroundImageFilePath;
}
string ItemData::getBackgroundImageFilePath()
{
    return m_backgroundImageFilePath;
}

void ItemData::setBottomBackGroundImageFilePath(string bottomPath)
{
    m_bottomBackGroundImageFilePath = bottomPath;
}

string ItemData::getBottomBackGroundImageFilePath()
{
    return m_bottomBackGroundImageFilePath;
}

void ItemData::setForegroundImageFilePath(string forePath)
{
    m_foregroundImageFilePath = forePath;
}

string ItemData::getForegroundImageFilePath()
{
    return m_foregroundImageFilePath;
}

void ItemData::setHintText(string hintText)
{
    m_hintText = hintText;
}

string ItemData::getHintText()
{
    return m_hintText;
}

void ItemData::setAction(string action)
{
    m_action = action;
}

string ItemData::getAction()
{
    return m_action;
}

void ItemData::setPackage(string package)
{
    m_package = package;
}

string ItemData::getPackage()
{
    return m_package;
}

void ItemData::setClass(string cls)
{
    m_class = cls;
}

string ItemData::getClass()
{
    return m_class;
}

void ItemData::setParmas(vector<Map<string, Ref*> > parmas)
{
    *m_parmas = parmas;
}

vector<Map<string, Ref*>>* ItemData::getParmas()
{
    return m_parmas;
}

void ItemData::setClipping(bool clp)
{
    m_Clipping = clp;
}

bool ItemData::getClipping()
{
    return m_Clipping;
}

void ItemData::setIsShow(bool show)
{
    m_isShow = show;
}

bool ItemData::getIsShow()
{
    return m_isShow;
}

void ItemData::setFocusScale(float focusScale)
{
    m_focusScale = focusScale;
}

float ItemData::getFocusScale()
{
    return m_focusScale;
}

void ItemData::setGravity(int gravity)
{
    m_gravity = gravity;
}

int ItemData::getGravity()
{
    return m_gravity;
}

void ItemData::setID(int ID)
{
    m_id = ID;
}

int ItemData::getID()
{
    return m_id;
}

void ItemData::setWidth(int width)
{
    m_width = width;
}

int ItemData::getWidth()
{
    return m_width;
}

void ItemData::setHeight(int height)
{
    m_height = height;
}

int ItemData::getHeight()
{
    return m_height;
}
