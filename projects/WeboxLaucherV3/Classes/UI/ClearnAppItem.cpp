/**
 * Copyright (C) 2014 Togic Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "ClearnAppItem.h"
#include "Utils/HandleMessageQueue.h"
#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include "../Utils/JniUtil.h"
#endif

ClearnAppItem::ClearnAppItem() :
		ro_label(nullptr), ro_end(nullptr), m_image_sm(nullptr), m_image_bg(
				nullptr), m_showAction(false) {

}

ClearnAppItem::~ClearnAppItem() {

}

ClearnAppItem* ClearnAppItem::create() {
	ClearnAppItem* item = new ClearnAppItem();
	if (item && item->init()) {
		item->autorelease();
		return item;
	}
	CC_SAFE_DELETE(item);
	return NULL;
}


bool ClearnAppItem::init()
{
	if (!AppItem::init())
	{
		return false;
	}

	m_image_bg = ui::ImageView::create();
	m_image_bg->setAnchorPoint(Vec2(0.5, 0.5));
	m_image_bg->setPosition(Vec2(ITEM_SIZE_APP.width / 2, 126));
	m_image_bg->loadTexture(APPITEM_CLEAN_Img);
	m_image_bg->setZOrder(999);
	this->addChild(m_image_bg);

	m_image_sm = ui::ImageView::create();
	m_image_sm->setAnchorPoint(Vec2(0.5, 0.5));
	m_image_sm->setPosition(Vec2(ITEM_SIZE_APP.width / 2, 126));
	m_image_sm->loadTexture(APPITEM_CLEAN_Img_Sm);
	m_image_sm->setZOrder(999);
	this->addChild(m_image_sm);

	DrawNode* shap = DrawNode::create();
	Vec2 point[4] = { Vec2(3, 50), Vec2(254, 50), Vec2(254, 75), Vec2(3, 75) };
	shap->drawPolygon(point, 4, Color4F(1, 1, 1, 1), 1,Color4F(1,1,1,1));
	ClippingNode* cliper = ClippingNode::create();
	cliper->setStencil(shap);
	cliper->setAlphaThreshold(1);
	cliper->setInverted(true);
	cliper->setAnchorPoint(Vec2::ZERO);
	cliper->setPosition(Vec2::ZERO);
	this->addChild(cliper);

	ro_label = ui::Text::create();
	ro_label->setTextHorizontalAlignment(TextHAlignment::CENTER);
	ro_label->setTextVerticalAlignment(TextVAlignment::CENTER);
	//    ro_label->enableShadow(Size(3, -3), 0.3 * 255, 3,Color3B::BLACK, true);
	ro_label->setColor(Color3B(255, 255, 255));
	ro_label->setPosition(AppItem_bottomBar_Position);
	ro_label->setFontSize(24);
	ro_label->setFontName( "fonts/FZLTZHK--GBK1-0.ttf");
	ro_label->setZOrder(2);
	this->addChild(ro_label);

	ro_end = ui::Text::create();
	ro_end->setTextHorizontalAlignment(TextHAlignment::CENTER);
	ro_end->setTextVerticalAlignment(TextVAlignment::CENTER);
	//    ro_end->enableShadow(Size(3, -3), 0.3 * 255, 3,Color3B::BLACK, true);
	ro_end->setColor(Color3B(255, 255, 255));
	ro_end->setPosition(AppItem_bottomBar_Position);
	ro_end->setFontSize(24);
	ro_end->setFontName("fonts/FZLTZHK--GBK1-0.ttf");
	ro_end->setZOrder(2);
	this->addChild(ro_end);

	HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
	handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(ClearnAppItem::getClearnMemory,this),"ClearMemory");

	this->setFocusEnabled(true);
	this->onFocusChanged = CC_CALLBACK_2(ClearnAppItem::onFocusChange, this);
	return true;
}

//void ClearnAppItem::itemFocused(ui::Widget* widget, bool isDown) {
////    if (!isDown ) {
////        m_pTitle->setPosition(AppItem_bottomBar_Position);
////    }
//}

void ClearnAppItem::onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus)
{
	//
//	log("@focus------------------------------------------------");
//	m_hintText->setVisible(true);
//	if(widgetLoseFocus != nullptr)
//	{
//		log("@widgetLose@focus------------------------------------------------");
//		m_hintText->setPosition(AppItem_bottomBar_Position);
//	}
}

void ClearnAppItem::getClearnMemory(std::string  memory) {
	if (!m_showAction)
	{
		return;
	}

	m_image_sm->stopAllActions();
	RotateBy *roteBy = RotateBy::create(0.02f, 500);
	EaseOut *easin = EaseOut::create(roteBy, 2);
	ScaleTo *scal1 = ScaleTo::create(0.02f, 1.0f);
	Sequence* seq = Sequence::createWithTwoActions(easin, scal1);
	m_image_sm->runAction(seq);

	ScaleTo *scal2 = ScaleTo::create(0.15f, 1.0f);
	m_image_bg->runAction(scal2);

	m_showAction = false;

	m_hintText->stopAllActions();
	ro_end->stopAllActions();
	ro_label->stopAllActions();

	ro_label->setPosition(Vec2(130, 30));
	MoveTo *move2 = MoveTo::create(0.1f, Vec2(ro_label->getPosition().x, 60));
	Sequence *seqrola = Sequence::createWithTwoActions(
			MoveTo::create(0.2f, Vec2(ro_label->getPosition().x, 30.1f)),
			move2);
	ro_label->runAction(seqrola);

	char str[100];
	sprintf(str, "已清理%.1f M内存", atof(memory.c_str()));
	ro_end->setString(str);
	ro_end->setVisible(true);
//    ro_end->setString((CCString::createWithFormat("已清理%.1f M内存",memory->floatValue())->getCString()));

	MoveTo *move1 = MoveTo::create(0.25f, Vec2(ro_end->getPosition().x, 30));
	Sequence* seqend = Sequence::create(move1,MoveTo::create(2.0f, Vec2(ro_end->getPosition().x, 30)), MoveTo::create(0.5f, Vec2(ro_end->getPosition().x, 60)), nullptr);
	ro_end->runAction(seqend);
	m_hintText->runAction(Sequence::createWithTwoActions(MoveTo::create(2.50f, AppItem_bottomBar_Position),Show::create()));
}

void ClearnAppItem::onEnterClicked(bool isLongPressed)
{
		if (m_showAction)
		{
			return;
		}
		m_showAction = true;
		m_hintText->stopAllActions();
		ro_end->stopAllActions();
		ro_label->stopAllActions();
		m_hintText->setVisible(false);
		m_hintText->setPosition(AppItem_bottomBar_Position);

		ro_end->setVisible(true);
		ro_label->setVisible(true);

		m_image_sm->stopAllActions();
		m_image_bg->stopAllActions();

		ScaleTo * scato1 = ScaleTo::create(0.07f, 1.2f);
		m_image_bg->runAction(scato1);

		ScaleTo * scato = ScaleTo::create(0.07f, 1.2f);
		RotateBy *roteBy = RotateBy::create(10.f, 9000);
		Sequence* seq = Sequence::createWithTwoActions(scato, roteBy);
		m_image_sm->runAction(seq);

		ro_end->setPosition(Vec2(130, 0));
		ro_end->setVisible(false);
		ro_label->setPosition(AppItem_bottomBar_Position);
		ro_label->setString("正在清理内存");

#if  (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
		JniUtil::getClearnMemoryJNI(true);
#endif
//		getClearnMemory("2");

}

