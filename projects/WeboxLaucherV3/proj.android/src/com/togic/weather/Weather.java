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

package com.togic.weather;

import com.togic.weboxlauncher.R;

import android.content.Context;
import android.util.Log;

/**
 * @author jar @date 2014年8月22日
 */
public class Weather {

    public String weather = "";
    public String weather2 = "";
    public String high = "";
    public String low = "";
    public String wind = "";
    public String image = "";

    public Weather(String weather, String weather2, String high, String low,
            String wind) {
        super();
        this.weather = weather;
        this.weather2 = weather2;
        this.high = high;
        this.low = low;
        this.wind = wind;
    }

    public Weather() {
        super();
    }

    protected void setResId(Context context) {
        if (weather.equals(context.getResources()
                .getString(R.string.str_cloudy))) {
            image = "cloudy_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_sunny))) {
            image = "sunny_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_smoke))) {
            image = "smoke_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_shade))) {
            image = "shade_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_h_sand_storm))) {
            image = "h_sand_storm_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_sand_storm))) {
            image = "sand_storm_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_fog))) {
            image = "smoke_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_sand_blowing))) {
            image = "sand_blowing_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_s_snow))) {
            image = "s_snow_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_m_snow))) {
            image = "m_snow_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_l_snow))) {
            image = "l_snow_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_h_snow))) {
            image = "h_snow_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_snow_shower))) {
            image = "snow_shower_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_s_rain))) {
            image = "s_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_m_rain))) {
            image = "m_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_l_rain))) {
            image = "l_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_h_rain))) {
            image = "h_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_hh_rain))) {
            image = "hh_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_hhh_rain))) {
            image = "hhh_rain_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_shower))) {
            image = "shower_720.png";
        } else if (weather.equals(context.getResources().getString(
                R.string.str_thunder_shower))) {
            image = "thunder_shower_720.png";
        } else {
            image = "sunny_720.png";
        }
    }

    @Override
    public String toString() {
        return "Weather:" + weather + "/" + weather2 + "  " + low + "/" + high
                + "  " + wind;
    }

}
