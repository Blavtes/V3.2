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

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

/**
 * @author jar @date 2014年8月22日
 */
public class WeatherFether {

    private static final String TAG = "WeatherFether";
    private static final String GET_CITY_IP_FROM_SINA = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js";
    public static final String DEFALUT_CITY_PATH = "/data/system/city.js";

    private ArrayList<IWeatherFether> mWeatherFethers = new ArrayList<WeatherFether.IWeatherFether>();
    private Weather mWeather = null;
    private Context mContext = null;

    private transient ExecutorService mThreadPool = Executors
            .newFixedThreadPool(2);

    public WeatherFether(Context context) {
        super();
        mContext = context;
    }

    private static String getCity() {
        InputStream in = null;
        try {
            in = FileUtil.getInputStreamFromUrl(GET_CITY_IP_FROM_SINA, 5000);
        } catch (Exception e) {
            return "";
        }
        if (in == null)
            return "";
        StringBuilder stringBuilder = FileUtil.readFromInputStream(in);
        if (stringBuilder == null || stringBuilder.length() == 0)
            return "";
        String string = stringBuilder.toString();
        string = string.replace("var remote_ip_info =", "").replace(";", "");
        try {
            JSONObject obj = new JSONObject(string);
            String city = obj.optString("city");
            if (city == null)
                return "";
            else
                return city;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Weather getweather(String name, String day) {
        Weather weather = new Weather();
        URL ur;
        try {

            DocumentBuilderFactory domfac = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc;
            Element root;
            NodeList books;

            ur = new URL("http://php.weather.sina.com.cn/xml.php?city="
                    + URLEncoder.encode(name, "gb2312")
                    + "&password=DJOYnieT8234jlsK&day=" + day);
            doc = dombuilder.parse(ur.openStream());
            root = doc.getDocumentElement();
            books = root.getChildNodes();
            if (books == null || books.item(1) == null) {
                weather.weather = "";
                weather.weather2 = "";
                weather.high = "";
                weather.low = "";
                weather.wind = "";
                return weather;
            }

            for (Node node = books.item(1).getFirstChild(); node != null; node = node
                    .getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("status1"))
                        weather.weather = node.getTextContent();
                    else if (node.getNodeName().equals("status2"))
                        weather.weather2 = node.getTextContent();
                    else if (node.getNodeName().equals("temperature1"))
                        weather.high = node.getTextContent();
                    else if (node.getNodeName().equals("temperature2"))
                        weather.low = node.getTextContent();
                    else if (node.getNodeName().equals("direction1"))
                        weather.wind = node.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weather;
    }

    public void registerIWeatherFether(IWeatherFether weatherFether) {
        mWeatherFethers.add(weatherFether);
        if (mWeather != null)
            weatherFether.localWeatherCallback(mWeather);
    }

    public void unRegisterIWeatherFether(IWeatherFether weatherFether) {
        mWeatherFethers.remove(weatherFether);
    }

    public void fenchWeather() {
        mThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                StringBuilder stringBuilder = FileUtil
                        .readFile(DEFALUT_CITY_PATH);
                if (stringBuilder == null || stringBuilder.length() == 0) {
                    Log.i(TAG, "The city js is not exist!");
                    getDefalutWeather();

                } else {
                    ArrayList<String> cities = jsonToArea(stringBuilder
                            .toString());
                    if (cities == null || cities.size() != 6) {
                        Log.i(TAG, "The city js is error!");
                        getDefalutWeather();
                    } else {
                        Weather weather = getweather(cities.get(2), "0");
                        if (weather.weather.length() == 0
                                || weather.high.length() == 0) {
                            Weather weather2 = getweather(cities.get(1), "0");
                            if (weather2.weather.length() == 0
                                    || weather2.high.length() == 0) {
                                Log.i(TAG,
                                        "Get the county weather and the city weather fail,then,get the weather from defalut!");
                                getDefalutWeather();
                            } else {
                                Log.i(TAG,
                                        "Get the county weather fail,then,get the city weather!");
                                setResAndNotify(weather2);
                            }
                        } else {
                            Log.i(TAG, "Get the county weather succeed!");
                            setResAndNotify(weather);
                        }

                    }
                }

            }
        });
    }

    private void setResAndNotify(Weather weather) {
        weather.setResId(mContext);
        notifyWeather(weather);
        mWeather = weather;
    }

    private void getDefalutWeather() {
        Weather weather = getweather(getCity(), "0");
        setResAndNotify(weather);
    }

    public static ArrayList<String> jsonToArea(String json) {
        ArrayList<String> areas = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject != null) {
                areas.add(jsonObject.optString("province"));
                areas.add(jsonObject.optString("city"));
                areas.add(jsonObject.optString("county"));
                areas.add(String.valueOf(jsonObject.optInt("provinceIndex")));
                areas.add(String.valueOf(jsonObject.optInt("cityIndex")));
                areas.add(String.valueOf(jsonObject.optInt("countyIndex")));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return areas;

    }

    public void fenchWeather(final String name, final String day) {
        mThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                Weather weather = getweather(name, day);
                weather.setResId(mContext);
                notifyWeather(weather);
                mWeather = weather;
            }
        });
    }

    public void fenchCity() {
        mThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                notifyCity(getCity());
            }
        });
    }

    private void notifyWeather(Weather weather) {
        for (IWeatherFether weatherFether : mWeatherFethers) {
            weatherFether.localWeatherCallback(weather);
        }
    }

    private void notifyCity(String city) {
        for (IWeatherFether weatherFether : mWeatherFethers) {
            weatherFether.localCityCallback(city);
        }
    }

    public interface IWeatherFether {
        public void localWeatherCallback(Weather weather);

        public void localCityCallback(String city);
    }

}
