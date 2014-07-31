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

package com.togic.weboxlauncher.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author mts @date 2013年12月16日
 */
public class CustomFontText extends TextView {

    public CustomFontText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CustomFontText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomFontText(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctx) {
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/FZLTZHK--GBK1-0.ttf");
        setTypeface(tf);
    }
}
