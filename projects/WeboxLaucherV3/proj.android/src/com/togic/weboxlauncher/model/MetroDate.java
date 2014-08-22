package com.togic.weboxlauncher.model;

import java.util.ArrayList;

import android.R.array;

//"version": 5,
//"background": "assets/image/launcher/Launcher1_01.jpg",
//"items":
	
public class MetroDate {
	
	MetroDate()
	{	
	}
	public int version;
	public String background;
	public MetroItemDate[] items;
	public Minfo[] toMinfos()
	{
		if(items.length > 0)
		{
			Minfo[] mfs = new Minfo[items.length];
			ArrayList<Minfo> list = new ArrayList<Minfo>();
			for(int i = 0; i < mfs.length; i++)
			{   
				MetroItemDate mid = items[i];
				Minfo mf = new Minfo();
			    mf.width = mid.width;
			    mf.height= mid.height;
			    mf.actionName= mid.action;
			    mf.label = mid.title;
			    mf.isShow= 0;
			    mf.backgroundImageFilePath = mid.background;
			    mf.foregroundImageFilePath = mid.icon;
			    mf.bottomBackGroundImageFilePath = mid.bottom_bg;
			    mf._id=mid._id;
			    list.add(mf);
			}
			
			list.toArray(mfs);
			
			return mfs;
			
		}
		
		return null;
	}

}
