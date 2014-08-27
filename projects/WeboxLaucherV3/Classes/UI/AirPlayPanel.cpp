/**
 * Copyright (C) 2013 Togic Corporation. All rights reserved.
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

#include "AirPlayPanel.h"
#include "PrefixConst.h"
#include "Utils/HandleMessageQueue.h"
#include "Utils/ParseJson.h"

AirPlayInfo::AirPlayInfo()
: m_author(nullptr)
, m_songName(nullptr)
, m_background(nullptr)
{
}

AirPlayInfo::~AirPlayInfo()
{
}

bool AirPlayInfo::init()
{
    if (!Layout::init())
    {
        return false;
    }

    m_background = ui::ImageView::create();
    m_background->loadTexture(AirPlay_Background_image);
    m_background->setAnchorPoint(Vec2::ZERO);
    m_background->setPosition(Vec2::ZERO);
    this->addChild(m_background);

    ui::ImageView *musicImage = ui::ImageView::create();
    musicImage->loadTexture(AirPlay_MusicMark_image);
    musicImage->setAnchorPoint(Vec2::ZERO);
    musicImage->setPosition(Vec2(28.0f, 25.0f));
    this->addChild(musicImage);

    m_author = ui::Text::create();
    m_author->setFontName("fonts/FZLTZHK--GBK1-0.ttf");
    m_author->setFontSize(28);
    m_author->setColor(Color3B::WHITE);
    m_author->setAnchorPoint(Vec2::ZERO);
    m_author->setTextHorizontalAlignment(TextHAlignment::LEFT);
    m_author->setTextVerticalAlignment(TextVAlignment::CENTER);
    m_author->setPosition(Vec2(112, 52));
    this->addChild(m_author);

    m_songName = ui::Text::create();
    m_songName->setFontName("fonts/FZLTZHK--GBK1-0.ttf");
    m_songName->setFontSize(16);
    m_songName->setColor(Color3B(235, 235, 235));
    m_songName->setAnchorPoint(Vec2::ZERO);
    m_songName->setTextHorizontalAlignment(TextHAlignment::LEFT);
    m_songName->setTextVerticalAlignment(TextVAlignment::CENTER);
    m_songName->setPosition(Vec2(m_author->getPosition().x, m_author->getPosition().y - 30));
    this->addChild(m_songName);

    return true;
}

ui::Text* AirPlayInfo::getAuthorLabel()
{
    return m_author;
}

ui::Text* AirPlayInfo::getSongNameLabel()
{
    return m_songName;
}

ui::ImageView* AirPlayInfo::getBackground()
{
    return m_background;
}


void AirPlayInfo::updataShowInfo(AirplayMusicData *data)
{
	char songName[100];
    if (data->m_intValue == AP_META || data->m_intValue == AP_START)
    {
        if (data->m_stringArtist.empty())
        {
            m_author->setString(AIRPLAY_DEFAULT_ARTIST);
        }
        else
        {
            m_author->setString(data->m_stringArtist.c_str());
        }

        if ( data->m_stringAlbum.empty()  && !data->m_stringTitle.empty())
        {
        	sprintf(songName,"<%s> ▪ %s", AIRPLAY_DEFAULT_ALBUM,data->m_stringTitle.c_str());
        	m_songName->setString(songName);
        }
        else if ( !data->m_stringAlbum.empty() && data->m_stringTitle.empty() )
        {
        	sprintf(songName,"<%s> ▪ %s", data->m_stringAlbum.c_str(),AIRPLAY_DEFAULT_TITLE);
        	m_songName->setString(songName);
        }
        else if (data->m_stringAlbum.empty() && data->m_stringTitle.empty() )
        {
        	sprintf(songName,"<%s> ▪ %s", AIRPLAY_DEFAULT_ALBUM,AIRPLAY_DEFAULT_TITLE);
        	m_songName->setString(songName);
        }
        else
        {
        	sprintf(songName,"<%s> ▪ %s",data->m_stringAlbum.c_str(),data->m_stringTitle.c_str());
        	m_songName->setString(songName);
        }
    }
}




//----------------------------------------------------AirPlayPanel------------------------------------------------


AirPlayPanel::AirPlayPanel()
: m_airPlayImage(nullptr)
, m_showInfo(nullptr)
{
}

AirPlayPanel::~AirPlayPanel()
{
}

bool AirPlayPanel::init()
{
    if ( !Layout::init() )
    {
        return false;
    }
    Size visibleSize = Director::getInstance()->getVisibleSize();

    m_showInfo = AirPlayInfo::create();
    m_showInfo->setAnchorPoint(Vec2(0.5, 0.5));
    m_showInfo->setPosition(Vec2(-700.0f, 20));
    this->addChild(m_showInfo);

    m_airPlayImage = ui::ImageView::create();
    m_airPlayImage->loadTexture(AirPlay_Icon);
    m_airPlayImage->setAnchorPoint(Vec2(0.5, 0.5));
    m_airPlayImage->setPosition(Vec2(960.0f, visibleSize.height - 75.0f));
    this->addChild(m_airPlayImage);
    m_airPlayImage->setVisible(false);

    HandleMessageQueue* handleMessage = HandleMessageQueue::getInstace();
    handleMessage->registerMsgCallbackFunc(CC_CALLBACK_1(AirPlayPanel::showAirPlayInfo,this),"AirPlay");

    return true;
}


void AirPlayPanel::moveIconPosition(bool isMove)
{
    float offset = 0;
    if (isMove)
    {
        offset = -55.0f;
    }
    m_airPlayImage->setPosition(Vec2(960 + offset,m_airPlayImage->getPosition().y));
}

ui::ImageView* AirPlayPanel::getAirPlayIcon()
{
    return m_airPlayImage;
}

AirPlayInfo *AirPlayPanel::getAirPlayInfo()
{
    return m_showInfo;
}

void AirPlayPanel::showAirPlayInfo(std::string jsonString)
{
	log("AirPlayPanel:::::Receive data from java notification is %s------------@airplay",jsonString.c_str());
	 Vector<AirplayMusicData*> airplayMusicData;
	 bool flag = ParseJson::getAirPlayMusicDataFromJSON(jsonString,airplayMusicData);
	 if(!flag)
	 {
		    m_airPlayImage->setVisible(false);
		    return;
	 }
	 if(airplayMusicData.size() == 0)
	 {
		 log("No airplayMusicData parsed from JSON-----------------------------@airplay");
		 return;
	 }
	 AirplayMusicData* musicData = airplayMusicData.at(0);
	 m_showInfo->updataShowInfo(musicData);

    if ( m_showInfo->getAuthorLabel()->getString().empty()  && m_showInfo->getSongNameLabel()->getString().empty())
    {
        return;
    }
	 log("data received successful!-----------------------------@airplay");
	m_airPlayImage->setVisible(true);
    float authorWidth = m_showInfo->getAuthorLabel()->getContentSize().width;
    float songWidth = m_showInfo->getSongNameLabel()->getContentSize().width;
    float offset = 0.0f;
    float scaleEnd = 0.0f;
    if (authorWidth > songWidth)
    {
        scaleEnd = authorWidth;
    }
    else
    {
        scaleEnd = songWidth;
    }
    offset = 112.0f+ scaleEnd + 28.0f;
    m_showInfo->getBackground()->setScaleX(offset / 330.0f);
    m_showInfo->stopAllActions();
    log("Begin to run Action, the position X is : %f------------------------------------@airplay",  offset);
    MoveTo *movebegin = MoveTo::create(0.5f, Vec2(40, 20));
    MoveTo *moveStop = MoveTo::create(3.0f, Vec2(40, 20));
    MoveTo *moveEnd = MoveTo::create(0.5f, Vec2(-offset, 20));
    Sequence *seq = Sequence::create(movebegin,moveStop,moveEnd,nullptr);
    m_showInfo->runAction(seq);
}
