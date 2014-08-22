package com.togic.weboxlauncher.model;

import java.util.ArrayList;

//"_id": 20,
//"width": 400,
//"height": 200,
//"action": "togic.intent.action.LIVE_TV",
//"gravity": 1,
//"clip": false,
//"background": "assets/image/item/item_tv_bg.png",
//"bottom_bg": "assets/image/item/item_tv_bottom_bg.png",
//"icon": "assets/image/item/icon_tv.png",
//"title": "",
//"isshow": -1
public class MetroItemDate {
	MetroItemDate()
	{
	}
	public int _id;
	public int width;
	public int height;
	public String action;
//	public int gravity;
	public String background;
	public String bottom_bg;
	public String icon;
	public String title;
//	public boolean isshow;	
	
	public String[] needDownloadUrls()
	{
		ArrayList<String> list = new ArrayList<String>();
		if(background!= null && background.length() > 0) list.add(background);
		if(bottom_bg!= null && bottom_bg.length() >0) list.add(bottom_bg);
		if(icon!= null && icon.length()> 0)list.add(icon);
		if(list.size() > 0)
		{
			String[] urls = new String[list.size()];
			list.toArray(urls);
			return urls;
		}
		return null;
	}

}
