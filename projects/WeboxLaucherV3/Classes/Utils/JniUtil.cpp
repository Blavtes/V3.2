//
//  JniUtil.cpp
//  WeBoxLauncher
//
//  Created by yong on 14-4-17.
//
//

#include "JniUtil.h"
#include "cocos2d.h"

#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include   "platform/android/jni/JniHelper.h"
#endif

USING_NS_CC;

#define  CLASS_NAME "org/cocos2dx/lib/Cocos2dxHelper"

#define  ACT_PKG_CLS 3

struct fields_intent {
    jclass      clazz;
    jmethodID   constructor;

    jmethodID   set_action;
    jmethodID   set_class;

    jmethodID   put_string;
    jmethodID   put_bool;
    jmethodID   put_int;
    jmethodID   put_float;
};

JniUtil::JniUtil()
{

}
JniUtil::~JniUtil()
{

}

void JniUtil::uninstallSilentJni(const char* pckname)
{
#if  (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, "com/togic/weboxlauncher/WeBoxLauncher", "uninstallPackage", "(Ljava/lang/String;)V")) {
        jstring string = t.env->NewStringUTF(pckname);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, string);
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(string);
    }
#endif
}

void JniUtil::onClickEvtJni(const char* pckname, const char* key)
{
#if  (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, "com/togic/weboxlauncher/WeBoxLauncher", "onClickEvt", "(Ljava/lang/String;Ljava/lang/String;)V")) {
        jstring string = t.env->NewStringUTF(pckname);
        jstring keys    = t.env->NewStringUTF(key);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, string, keys);
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(string);
        t.env->DeleteLocalRef(keys);
    }
#endif
}

const char* JniUtil::getJsonInfoFromLocalJni()
{
#if  (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, "com/togic/weboxlauncher/WeBoxLauncher", "getJsonInfosFromLocal", "()Ljava/lang/String;")) {
        jstring infos = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID,NULL);
        t.env->DeleteLocalRef(t.classID);
        return t.env->GetStringUTFChars(infos,NULL);
    }
#endif
}

void* JniUtil::getIconDataWithPackage(const char* pack)
{
#if  (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo t;

    if (JniHelper::getStaticMethodInfo(t, "com/togic/weboxlauncher/WeBoxLauncher", "getIconDataWithPackage", "(Ljava/lang/String;)[B")) {
         jstring string = t.env->NewStringUTF(pack);

       jarray excludedDeviceNames = jarray(t.env->CallStaticObjectMethod(t.classID, t.methodID,string));
        jbyte* arrayBody = t.env->GetByteArrayElements((jbyteArray)excludedDeviceNames,0);
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(string);

        return (void*)arrayBody;
    }
#endif
}

bool  JniUtil::startActivityJNI(const char* action, const char* pkgName, const char* className) {
    if (action == NULL && (pkgName == NULL || className == NULL)) {
        CCLOGWARN("can't init Intent by action or package & class name");
        return false;
    }
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, "com/togic/weboxlauncher/WeBoxLauncher", "mStartActivity", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")) {
        jstring jaction = t.env->NewStringUTF(action);
        jstring jpkgName    = t.env->NewStringUTF(pkgName);
        jstring jclassName  = t.env->NewStringUTF(className);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, jaction, jpkgName,jclassName);
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(jaction);
        t.env->DeleteLocalRef(jpkgName);
        t.env->DeleteLocalRef(jclassName);
    }
    return true;
}

