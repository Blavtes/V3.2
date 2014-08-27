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

#ifndef __WeBoxLauncher__AirplayMusicData__
#define __WeBoxLauncher__AirplayMusicData__

#include "cocos2d.h"
#include "json/document.h"
USING_NS_CC;

using namespace std;

class AirplayMusicData : public Ref
{
public:
    AirplayMusicData();
    ~AirplayMusicData();
    static AirplayMusicData* create( const rapidjson::Value& jsonItem);
    virtual bool init(const rapidjson::Value& jsonItem);

public:
    int      m_eventType;
    int      m_intValue;
    int      m_longValue;
    double    m_floatValue;
    string   m_stringValue;
    string   m_stringSession;
    string   m_stringCoverart;
    string   m_stringTitle;
    string   m_stringAlbum;
    string   m_stringArtist;
    bool     m_booleanValue;
};

#endif /* defined(__WeBoxLauncher__AirplayMusicData__) */
