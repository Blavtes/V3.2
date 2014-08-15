/*
 * LayerProtocal.h
 *
 *  Created on: 2014年8月7日
 *      Author: xjx
 */

#ifndef RECEIVEMESSAGEPROTOCOL_H_
#define RECEIVEMESSAGEPROTOCOL_H_
#include "cocos2d.h"
USING_NS_CC;

class  ReceiveMessageProtocol
{
public:
    virtual ~ReceiveMessageProtocol() {}
    virtual void receiveMessageData(std::string jsonString)=0;
    virtual void setMessageTitle(std::string messageTitle){ m_messageTitle = messageTitle;}
    virtual std::string getMessageTitle() { return m_messageTitle;}
protected:
    std::string m_messageTitle;

};



#endif /* RECEIVEMESSAGEPROTOCOL_H_ */
