/*
 * ToastTextView.h
 *
 *  Created on: 2014年8月18日
 *      Author: xjx
 */

#ifndef TOASTTEXTVIEW_H_
#define TOASTTEXTVIEW_H_

#include "cocos2d.h"
USING_NS_CC;



class MsgD{
public:
	MsgD()
	{
		msg = "";
		time = 0;
		parent = nullptr;
		messagePos = Vec2::ZERO;
	}
	MsgD(std::string m, int t , Layer* p,Vec2 pos)
	{
		msg = m;
		time = t;
		parent = p;
		messagePos = pos;
	}
	 ~MsgD(){}


     std::string msg;
	int time;
	Layer* parent;
	 Vec2 messagePos;
protected:
};


class ToastTextView: public cocos2d::ui::Text {
public:
	ToastTextView();
	virtual ~ToastTextView();
	virtual bool init();
	 void showMsg(std::string msg, int time, Layer* parent,Vec2 pos=Vec2::ZERO);
	static ToastTextView*  getInstance(Layer* parent);
protected:
	static ToastTextView* mInstance;
	static Layer* mLayer;
	std::queue<MsgD*>* MsgQ;
	 void showMsg();
	  void clearMsg(float dt);
	 bool isShowing;
};


#endif /* TOASTTEXTVIEW_H_ */
