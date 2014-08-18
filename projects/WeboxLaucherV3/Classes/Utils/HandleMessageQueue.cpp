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

map<string , ReceiveMessageProtocol* > HandleMessageQueue::m_layerMap;
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
	//
//    Scheduler* scheduler = Director::getInstance()->getScheduler();        //----------------基于定时器回调函数的形式
//    scheduler->schedule(schedule_selector(HandleMessageQueue::handleMessage),this,1,false);

    std::thread t1(&HandleMessageQueue::startThread,this);  //------基于多线程的方式
    t1.detach();
	return true;
}


void HandleMessageQueue::registerLayer(ReceiveMessageProtocol* layer, string messageType)

{
	//......
	if(layer == nullptr)
	{
		log("The registered layer is null!");
		return;
	}

	m_layerMap.insert(pair<string,ReceiveMessageProtocol*>(messageType,layer));

}


void HandleMessageQueue::handleMessage(float dt)
{

//	int messageCount = m_messageQueue.size();
//	while(!m_messageQueue.empty() && messageCount > 0)
//	{
//		bool flag = true;
//		map<string, string> message = m_messageQueue.front();
//		string messageTitle = (*message.begin()).first;
//		string messageContent = (*message.begin()).second;
//		log("The message will be processed by Layer:%s-----------------xjx", messageTitle.c_str());
////		ReceiveMessageProtocol* testLayer =(ReceiveMessageProtocol*) m_layerVector.front();
//		for(auto member : m_layerMap)
//		{
//			if( layer != nullptr && messageTitle.compare(member.first) == 0 )
//			{
//				//
//				ReceiveMessageProtocol* hello =( ReceiveMessageProtocol*)layer;
//				hello->receiveMessageData(messageTitle,messageContent );
//				m_messageQueue.pop();
//				flag = false;
//			}
//		}
//		if(flag)
//		{
//			m_messageQueue.pop();
//			m_messageQueue.push(message);
//		}
//		messageCount -= 1;
//	}
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

					for(auto layer : m_layerMap)
					{
							if(messageTitle.compare(layer.first) == 0 )
							{
								//
								Director::getInstance()->getScheduler()->performFunctionInCocosThread( [arg,layer,messageTitle,messageContent]
								{
										(layer.second)->receiveMessageData(messageTitle,messageContent );

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




