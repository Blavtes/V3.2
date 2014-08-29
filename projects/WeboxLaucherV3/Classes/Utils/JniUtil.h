//
//  JNIUtil.h
//  WeBoxLauncher
//
//  Created by yong on 14-4-17.
//
//

#ifndef __WeBoxLauncher__JniUtil__
#define __WeBoxLauncher__JniUtil__
#include "cocos2d.h"
USING_NS_CC;

class JniUtil{

public:
	JniUtil();
	~JniUtil();
	static void uninstallSilentJni(const char* pckname);
	static void onClickEvtJni(const char* pckname,const char* key);
	static const char* getJsonInfoFromLocalJni(void);

	static void* getIconDataWithPackage(const char* pack);
	static bool startActivityJNI(const char* action, const char* pkgName, const char* clsName);
	static void getClearnMemoryJNI(bool issystem);

	    static const char* beginAutoStartActivity();
	    static void sendBroadcastToSettingShowTV();
};
#endif /* defined(__WeBoxLauncher__JniUtil__) */

