/*
 * SizeTo.cpp
 *
 *  Created on: 2014年8月22日
 *      Author: xjx
 */

#include "SizeTo.h"

SizeTo::SizeTo()
{
    m_fSizeW = 0;
    m_fSizeH = 0;
    m_fStartSizeW=0;
    m_fStartSizeH=0;
    m_fEndSizeW=0;
    m_fEndSizeH=0;
    m_fDeltaW=0;
    m_fDeltaH=0;
}

SizeTo::~SizeTo()
{
	// ..................
}



SizeTo* SizeTo::create(float duration, float width, float height)
	{
	    SizeTo *pSizeTo = new SizeTo();
	    if(pSizeTo && pSizeTo->initWithDuration(duration, width, height))
	    {
	    		pSizeTo->autorelease();
	    		return pSizeTo;
	    }
	    return nullptr;
	}

	bool SizeTo::initWithDuration(float duration, float width, float height)
	{
	    if (ActionInterval::initWithDuration(duration))
	    {
	        m_fEndSizeW = width;
	        m_fEndSizeH = height;

	        return true;
	    }

	    return false;
	}


	void SizeTo::startWithTarget(Node *pTarget)
	{
		log("set the size of target-------------------@action");
	    ActionInterval::startWithTarget(pTarget);
	    m_fStartSizeW = pTarget->getContentSize().width;
	    m_fStartSizeH = pTarget->getContentSize().height;

	    m_fDeltaW = m_fEndSizeW - m_fStartSizeW;
	    m_fDeltaH = m_fEndSizeH - m_fStartSizeH;
	}

	void SizeTo::update(float time)
	{
		log("Action,Size To is begin updating!!!!!===========================@action!");
	    if (_target && (m_fDeltaW || m_fDeltaH))
	    {
	    	_target->setContentSize(Size(m_fStartSizeW + m_fDeltaW * time, m_fStartSizeH + m_fDeltaH * time ));
	    }
	}



SizeTo* SizeTo::clone() const
{
	// no copy constructor
	auto a = new SizeTo();
    a->initWithDuration(_duration, m_fEndSizeW, m_fEndSizeH);
	a->autorelease();
	return a;
}


SizeTo* SizeTo::reverse() const
{
    return nullptr;
}

 void SizeTo::step(float dt)
{
	//
	 log("step is calling -----------------------------@action!");
}


