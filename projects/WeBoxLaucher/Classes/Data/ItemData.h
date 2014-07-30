/*
 * ItemData.h
 *
 *  Created on: 2014年7月15日
 *      Author: xjx
 */

#ifndef ITEMDATA_H_
#define ITEMDATA_H_

#include "cocos2d.h"

class ItemData: public cocos2d::Ref {
public:
	ItemData();
	virtual ~ItemData();
	static ItemData* create();
	virtual bool init();

	void setBackgroundImageFilePath(const char* backgroundImageFilePath);
	const char* getBackgroundImageFilePath();



private:
	const char* m_backgroundImageFilePath;
	const char*  m_foregroundImageFilePath;
	const char* m_bottomBackGroundImageFilePath;
	const char* m_hintText;
	bool m_isClip;
	int m_id;
};

#endif /* ITEMDATA_H_ */
