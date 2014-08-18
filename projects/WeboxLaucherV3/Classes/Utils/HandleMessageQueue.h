/*
 * HandleMessageQueue.h
 *
 *  Created on: 2014年8月6日
 *      Author: xjx
 */

#ifndef HANDLEMESSAGEQUEUE_H_
#define HANDLEMESSAGEQUEUE_H_
#include "cocos2d.h"
#include "../UI/ReceiveMessageProtocol.h"
USING_NS_CC;
using namespace std;

class HandleMessageQueue : public Ref {
public:
	HandleMessageQueue();
	virtual ~HandleMessageQueue();
	static HandleMessageQueue* getInstace();
	virtual bool init();


	static void registerLayer(ReceiveMessageProtocol* layer,string messageType);
	static void pushMessage(string messageType,string messageContent);
	void handleMessage(float dt);

	 static void startThread(void* arg);
	 static void setIsBackground(bool);
protected:
	 static bool isBackground;
	 static HandleMessageQueue* intentHandleMessageQueue;
	static queue< map<string,string> >  m_messageQueue;

	static map<string , ReceiveMessageProtocol* >  m_layerMap;
};

#endif /* HANDLEMESSAGEQUEUE_H_ */
