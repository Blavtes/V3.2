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
 vector<ReceiveMessageProtocol*> HandleMessageQueue:: m_layerVector;
 mutex messageMutex;
 HandleMessageQueue* HandleMessageQueue::intentHandleMessageQueue = NULL;
 bool HandleMessageQueue::isBackground = false;
HandleMessageQueue::HandleMessageQueue()
{
//	init();
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

void HandleMessageQueue::registerLayer(ReceiveMessageProtocol* layer)
{
	//......
	if(layer == nullptr)
	{
		log("The registered layer is null!");
		return;
	}
	m_layerVector.push_back(layer);
}


void HandleMessageQueue::handleMessage(float dt)
{
	//
	int messageCount = m_messageQueue.size();
	while(!m_messageQueue.empty() && messageCount > 0)
	{
		bool flag = true;
		map<string, string> message = m_messageQueue.front();
		string messageTitle = (*message.begin()).first;
		string messageContent = (*message.begin()).second;
		log("The message will be processed by Layer:%s-----------------xjx", messageTitle.c_str());
		ReceiveMessageProtocol* testLayer =(ReceiveMessageProtocol*) m_layerVector.front();
		for(auto layer : m_layerVector)
		{
			if( layer != nullptr && messageTitle.compare(layer->getMessageTitle()) == 0 )
			{
				//
				ReceiveMessageProtocol* hello =( ReceiveMessageProtocol*)layer;
				hello->receiveMessageData(messageContent );
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
	}
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
	//......
//	HandleMessageQueue* handleMessageQueue =(HandleMessageQueue*)arg;
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
//					sleep(5);
					messageMutex.lock();
					bool flag = true;
					map<string, string> message = m_messageQueue.front();
					string messageTitle = (*message.begin()).first;
					string messageContent = (*message.begin()).second;
					log("The message is %s,  will be processed by Layer:%s-----------------xjx", messageContent.c_str(), messageTitle.c_str());
					for(auto layer : m_layerVector)
					{
							if(messageTitle.compare(layer->getMessageTitle()) == 0 )
							{
								//
								Director::getInstance()->getScheduler()->performFunctionInCocosThread( [arg,layer,messageContent]
								{
										layer->receiveMessageData(messageContent );
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

void HandleMessageQueue::startThread2(void* arg)
{
	//
	static int i = 0;
	while(true)
	{
		//
		HandleMessageQueue::pushMessage("MainLayer","new message");
		log("-------------------%d-----------------xjx",i++);
		sleep(3);
	}
}

 void HandleMessageQueue::setIsBackground(bool isback)
 {
	 isBackground = isback;
 }




