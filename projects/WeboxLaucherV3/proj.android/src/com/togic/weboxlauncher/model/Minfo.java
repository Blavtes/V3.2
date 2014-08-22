package com.togic.weboxlauncher.model;

public class Minfo extends BaseInfo {
	Minfo() {
	}

	public Minfo(CombData id) {
		 width = id.width;
		 height= id.height;
		 actionName= id.action;
		 packageName= id.packageName;
		 label = id.title;
		 className= id.className;
		 isShow= id.isshow;
		 visible= id.visible;
		backgroundImageFilePath = id.background;
		foregroundImageFilePath = id.icon;
		bottomBackGroundImageFilePath = id.bottomBg;

		_id=id.id;
		category_tag = id.category_tag;
	}
	 public int isShow;
	 public int visible;
	public String backgroundImageFilePath;
	public String foregroundImageFilePath;
	public String bottomBackGroundImageFilePath;

	public int _id;
	public String category_tag;
}
