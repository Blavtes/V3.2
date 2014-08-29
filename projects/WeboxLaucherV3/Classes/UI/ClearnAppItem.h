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

#ifndef __WeBoxLauncher__ClearnAppItem__
#define __WeBoxLauncher__ClearnAppItem__

#include "cocos2d.h"
#include "AppItem.h"

USING_NS_CC;

class ClearnAppItem : public AppItem
{
public:
    ClearnAppItem();
    ~ClearnAppItem();
    static ClearnAppItem* create();
    virtual bool init();
    void onEnterClicked(bool isLongPressed);
//    void itemFocused(ui::Widget* widget, bool isDown);
    void onFocusChange(ui::Widget* widgetLoseFocus, ui::Widget* widgetGetFocus);
    void getClearnMemory(std::string  memory);
private:
    ui::Text* ro_label;
    ui::Text* ro_end;
    ui::ImageView* m_image_sm;
    ui::ImageView* m_image_bg;
    bool m_showAction;
};

#endif /* defined(__WeBoxLauncher__ClearnAppItem__) */
