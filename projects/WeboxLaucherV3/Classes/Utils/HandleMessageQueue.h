/*
 * HandleMessageQueue.h
 *
 *  Created on: 2014年8月6日
 *      Author: xjx
 */

#ifndef HANDLEMESSAGEQUEUE_H_
#define HANDLEMESSAGEQUEUE_H_
#include "cocos2d.h"
USING_NS_CC;
using namespace std;

typedef std::function<void(std::string)> MsgCallbackFunc;
class HandleMessageQueue : public Ref {
public:
	HandleMessageQueue();
	virtual ~HandleMessageQueue();
	static HandleMessageQueue* getInstace();
	virtual bool init();

	static void registerMsgCallbackFunc(MsgCallbackFunc layer,string messageType);
	static void pushMessage(string messageType,string messageContent);
	 static void startThread(void* arg);
	 static void setIsBackground(bool);
protected:
	 static bool isBackground;
	 static HandleMessageQueue* intentHandleMessageQueue;
	static queue< map<string,string> >  m_messageQueue;
	static map<string , MsgCallbackFunc >  m_funcMap;
};
#endif /* HANDLEMESSAGEQUEUE_H_ */
