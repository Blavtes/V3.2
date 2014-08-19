/*
 * ToastTextView.cpp
 *
 *  Created on: 2014年8月18日
 *      Author: xjx
 */

#include "ToastTextView.h"
USING_NS_CC;

ToastTextView* ToastTextView::mInstance = nullptr;
Layer* ToastTextView::mLayer = nullptr;
ToastTextView::ToastTextView()
{
	MsgQ = new std::queue<MsgD*>();
	isShowing = false;
}

ToastTextView* ToastTextView::getInstance(Layer* parent)
{
	if(nullptr == mInstance || mLayer != parent)
	{
		mInstance = new ToastTextView();
		mLayer = parent;
		if(mInstance && mInstance->init())
		{
			parent->addChild(mInstance);
		}
		else
		{
			mInstance = nullptr;
		}
//		parent->addChild(mInstance);
	}
	return mInstance;
}

 bool ToastTextView::init()
{
	//
	 if(!ui::Text::init())
	 {
		 return false;
	 }
	 return true;
}

void ToastTextView::showMsg(std::string msg, int time, Layer* p,Vec2 pos)
{
	log("add message %s================@xjx",msg.c_str());
     MsgD* md = new MsgD(msg,time,p,pos);
     MsgQ->push(md);
     log("The message Queue size is:%d=========@xjx",MsgQ->size());
     showMsg();
}

void ToastTextView::showMsg()
{
	if(isShowing)
		return;

    if(nullptr != MsgQ && MsgQ->size() > 0)
    {
    	isShowing = true;
    	MsgD* md = MsgQ->front();
    	log("The show message is:%s=================@xjx",md->msg.c_str());
    	this->setString(md->msg);
    	this->setFontSize(30);
    	this->setPosition(md->messagePos);
    	this->setVisible(true);
    	this->schedule(schedule_selector(ToastTextView::clearMsg), md->time);
    }
}

void ToastTextView::clearMsg(float dt)
{
	MsgD* md = MsgQ->front();
	log("The deleted message is:%s=================@xjx",md->msg.c_str());
    if(nullptr != md)
    {
//    	md->parent->removeChild(this,false);
    	this->setVisible(false);
    	this->unschedule(schedule_selector(ToastTextView::clearMsg));
    }
    delete md;
    isShowing = false;
    MsgQ->pop();
    showMsg();
}


ToastTextView::~ToastTextView()
{
	delete MsgQ;
	mInstance->release();
}

