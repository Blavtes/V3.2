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

#ifndef __WeBoxLauncher__AirPlayPanel__
#define __WeBoxLauncher__AirPlayPanel__

#include "cocos2d.h"
#include "Data/AirplayMusicData.h"

USING_NS_CC;

class AirPlayInfo  : public ui::Layout
{
public:
    AirPlayInfo();
    ~AirPlayInfo();
    CREATE_FUNC(AirPlayInfo);

    virtual bool init();
    void updataShowInfo(AirplayMusicData *data);
    ui::Text * getAuthorLabel();
    ui::Text * getSongNameLabel();
    ui::ImageView * getBackground();
protected:
    ui::Text *m_author;
    ui::Text *m_songName;
    ui::ImageView *m_background;
};


class AirPlayPanel : public ui::Layout
{
public:

    AirPlayPanel();
    ~AirPlayPanel();
    CREATE_FUNC(AirPlayPanel);

    virtual bool init();

    void showAirPlayInfo(std::string jsonString);
//    void moveIconPosition(bool isMove);
    AirPlayInfo *getAirPlayInfo();
    ui::ImageView *getAirPlayIcon();
    void hideAirPlayImage(float dt);
protected:
    AirPlayInfo *m_showInfo;
    ui::ImageView *m_airPlayImage;
};

#endif /* defined(__WeBoxLauncher__AirPlayPanel__) */
