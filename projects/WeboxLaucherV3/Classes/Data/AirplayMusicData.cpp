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

#include "AirplayMusicData.h"

AirplayMusicData::AirplayMusicData() :
		m_eventType(-1), m_intValue(-1), m_longValue(-1), m_floatValue(
		                -0.1f), m_stringTitle(""), m_stringArtist(""), m_stringAlbum(
		                ""), m_stringValue(""), m_stringSession(""), m_stringCoverart(
		                ""), m_booleanValue(false)
{

}

AirplayMusicData::~AirplayMusicData()
{

}

AirplayMusicData* AirplayMusicData::create(const rapidjson::Value& jsonItem)
{
	AirplayMusicData *airdata = new AirplayMusicData();
	if (airdata && airdata->init(jsonItem))
	{
		airdata->autorelease();
		return airdata;
	}
	delete airdata;
	return NULL;
}

bool AirplayMusicData::init(const rapidjson::Value& jsonItem)
{
	if (jsonItem.IsObject())
	{
		if (jsonItem.HasMember("eventType"))
		{
			m_eventType = jsonItem["eventType"].GetInt();
		}
		if (jsonItem.HasMember("intValue"))
		{
			m_intValue = jsonItem["intValue"].GetInt();
		}
		if (jsonItem.HasMember("longValue"))
		{
			m_longValue = jsonItem["longValue"].GetInt();
		}
		if (jsonItem.HasMember("floatValue"))
		{
			m_floatValue = jsonItem["floatValue"].GetDouble();
		}
		if (jsonItem.HasMember("stringValue"))
		{
			m_stringValue = jsonItem["stringValue"].GetString();
		}
		if (jsonItem.HasMember("stringSession"))
		{
			m_stringSession = jsonItem["stringSession"].GetString();
		}
		if (jsonItem.HasMember("stringCoverart"))
		{
			m_stringCoverart =
			                jsonItem["stringCoverart"].GetString();
		}
		if (jsonItem.HasMember("stringTitle"))
		{
			m_stringTitle = jsonItem["stringTitle"].GetString();
		}
		if (jsonItem.HasMember("stringAlbum"))
		{
			m_stringAlbum = jsonItem["stringAlbum"].GetString();
		}
		if (jsonItem.HasMember("stringArtist"))
		{
			m_stringArtist = jsonItem["stringArtist"].GetString();
		}
		if (jsonItem.HasMember("booleanValue"))
		{
			m_booleanValue = jsonItem["booleanValue"].GetBool();
		}
	}
	return true;
}
