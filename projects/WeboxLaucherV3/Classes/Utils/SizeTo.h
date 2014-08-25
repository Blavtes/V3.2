/*
 * SizeTo.h
 *
 *  Created on: 2014年8月22日
 *      Author: xjx
 */

#ifndef SIZETO_H_
#define SIZETO_H_

#include "cocos2d.h"
USING_NS_CC;

class SizeTo: public ActionInterval
{
public:
	SizeTo();
	virtual ~SizeTo();
    static SizeTo* create(float duration, float width, float height);
    bool initWithDuration(float duration,float width,float height);
    virtual void startWithTarget(Node *pTarget);
    virtual void update(float time);
    virtual void step(float dt);
    virtual SizeTo* clone() const override;
	virtual SizeTo* reverse(void) const  override;
protected:
    float m_fSizeW;
    float m_fSizeH;
    float m_fStartSizeW;
    float m_fStartSizeH;
    float m_fEndSizeW;
    float m_fEndSizeH;
    float m_fDeltaW;
    float m_fDeltaH;

};

#endif /* SIZETO_H_ */

