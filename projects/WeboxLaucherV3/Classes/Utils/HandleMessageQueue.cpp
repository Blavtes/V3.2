/*
 * HandleMessageQueue.cpp
 *
 *  Created on: 2014年8月6日
 *      Author: xjx
 */

#include "HandleMessageQueue.h"
#include <thread>
#include <unistd.h>
using namespace std;

 queue< map<string,string> >  HandleMessageQueue::m_messageQueue;
map<string , MsgCallbackFunc > HandleMessageQueue::m_funcMap;
 mutex messageMutex;
 HandleMessageQueue* HandleMessageQueue::intentHandleMessageQueue = NULL;
 bool HandleMessageQueue::isBackground = false;
HandleMessageQueue::HandleMessageQueue()
{

}

HandleMessageQueue::~HandleMessageQueue()
{
	//...................
}

 HandleMessageQueue* HandleMessageQueue::getInstace()
{
	 if(NULL == intentHandleMessageQueue)
	 {
		 intentHandleMessageQueue = new HandleMessageQueue();
		 if(intentHandleMessageQueue && intentHandleMessageQueue->init())
		 {
			 intentHandleMessageQueue->autorelease();
			 return intentHandleMessageQueue;
		 }
		 CC_SAFE_DELETE(intentHandleMessageQueue);
		 return nullptr;
	 }
	return intentHandleMessageQueue;
}


bool HandleMessageQueue::init()
{
    std::thread t1(&HandleMessageQueue::startThread,this);  //------基于多线程的方式
    t1.detach();
	return true;
}


void HandleMessageQueue::registerMsgCallbackFunc(MsgCallbackFunc layer, string messageType)
{
	//......
	if(layer == nullptr)
	{
		log("The registered layer is null!");
		return;
	}
	m_funcMap.insert(pair<string,MsgCallbackFunc>(messageType,layer));
}


void HandleMessageQueue::pushMessage(string messageTitle, string messageContent)
{
	//.......
	messageMutex.lock();
	map<string,string> map0;
	map0.insert(pair<string,string>(messageTitle,messageContent));
	m_messageQueue.push(map0);
	messageMutex.unlock();
}

void HandleMessageQueue::startThread(void* arg)
{

	while(true)
	{
			sleep(1);
			if(isBackground)
			{
				continue;
			}
			int messageCount = m_messageQueue.size();
			while(!m_messageQueue.empty() && messageCount > 0)
			{

					messageMutex.lock();
					bool flag = true;
					map<string, string> message = m_messageQueue.front();
					string messageTitle = (*message.begin()).first;
					string messageContent = (*message.begin()).second;

					log("@xjx ==================== start thread - %d-  %s ", messageCount ,messageTitle.c_str());

					for(auto func : m_funcMap)
					{
							if(messageTitle.compare(func.first) == 0 )
							{
								//
								Director::getInstance()->getScheduler()->performFunctionInCocosThread( [arg,func,messageTitle,messageContent]
								{
//										(layer.second)->receiveMessageData(messageTitle,messageContent );
										(func.second)(messageContent);
								 } );
								m_messageQueue.pop();
								flag = false;
						   }
					}
					if(flag)
					{
							m_messageQueue.pop();
							m_messageQueue.push(message);
					}
					messageCount -= 1;
					messageMutex.unlock();
			}
	}
}


 void HandleMessageQueue::setIsBackground(bool isback)
 {
	 isBackground = isback;
 }




