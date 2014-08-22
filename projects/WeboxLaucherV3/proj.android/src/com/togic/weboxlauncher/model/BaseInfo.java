package com.togic.weboxlauncher.model;

import com.togic.weboxlauncher.app.AppInfo;

public class BaseInfo {
	BaseInfo() {
	}

	public BaseInfo(AppInfo aif) {
		width = aif.iconWidth;
		height = aif.iconHeight;
		actionName = aif.getClassName();
		packageName = aif.getPackageName();
		label = aif.label;
		className = aif.getClassName();

		proflag = aif.proFlag;
	}

	public int proflag;
	public int width;
	public int height;
	public String actionName;
	public String packageName;
	public String label;
	public String className;
}
