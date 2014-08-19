#include "AppDelegate.h"
#include "cocos2d.h"
#include "platform/android/jni/JniHelper.h"
#include "base/CCDirector.h"
#include "base/CCEventKeyboard.h"
#include <jni.h>
#include <android/log.h>
#include "UI/MainLayer.h"
#include "Utils/HandleMessageQueue.h"

#define  LOG_TAG    "main"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

using namespace cocos2d;

void cocos_android_app_init(JNIEnv* env, jobject thiz) {
	LOGD("cocos_android_app_init");
	AppDelegate *pAppDelegate = new AppDelegate();
}
#define KEYCODE_BACK 0x04
#define KEYCODE_MENU 0x52
#define KEYCODE_DPAD_UP 0x13
#define KEYCODE_DPAD_DOWN 0x14
#define KEYCODE_DPAD_LEFT 0x15
#define KEYCODE_DPAD_RIGHT 0x16
#define KEYCODE_ENTER 0x42
#define KEYCODE_PLAY  0x7e
#define KEYCODE_DPAD_CENTER  0x17

static std::unordered_map<int, cocos2d::EventKeyboard::KeyCode> g_keyCodeMap = {
		{ KEYCODE_BACK, cocos2d::EventKeyboard::KeyCode::KEY_ESCAPE }, {
				KEYCODE_MENU, cocos2d::EventKeyboard::KeyCode::KEY_MENU }, {
				KEYCODE_DPAD_UP, cocos2d::EventKeyboard::KeyCode::KEY_DPAD_UP },
		{ KEYCODE_DPAD_DOWN, cocos2d::EventKeyboard::KeyCode::KEY_DPAD_DOWN }, {
				KEYCODE_DPAD_LEFT,
				cocos2d::EventKeyboard::KeyCode::KEY_DPAD_LEFT }, {
				KEYCODE_DPAD_RIGHT,
				cocos2d::EventKeyboard::KeyCode::KEY_DPAD_RIGHT }, {
				KEYCODE_ENTER, cocos2d::EventKeyboard::KeyCode::KEY_ENTER }, {
				KEYCODE_PLAY, cocos2d::EventKeyboard::KeyCode::KEY_PLAY }, {
				KEYCODE_DPAD_CENTER,
				cocos2d::EventKeyboard::KeyCode::KEY_DPAD_CENTER },

};

extern "C" {

jboolean Java_com_togic_weboxlauncher_MCocos2dxGLSurfaceView_nativeKeyEvent(JNIEnv * env,
		jobject thiz, jint keyCode, jint keyevent) {
	Director* pDirector = Director::getInstance();

	auto iterKeyCode = g_keyCodeMap.find(keyCode);
	if (iterKeyCode == g_keyCodeMap.end()) {
		return JNI_FALSE;
	}

	cocos2d::EventKeyboard::KeyCode cocos2dKey = g_keyCodeMap.at(keyCode);

	log("@xjx--- nativeKeyEvent %d  action %d",cocos2dKey, keyevent);

	cocos2d::EventKeyboard event(cocos2dKey, (0==keyevent)?true:false);
	cocos2d::Director::getInstance()->getEventDispatcher()->dispatchEvent(&event);
	return JNI_TRUE;
}

jboolean Java_com_togic_weboxlauncher_WeBoxLauncher_nativeJsonString(
		JNIEnv * env, jobject thiz, jstring str, jstring dest) {
	const char *content = env->GetStringUTFChars(str,NULL);
	const char *destItem = env->GetStringUTFChars(dest,NULL);
	log("@msg ==================== jni  %s  : %s=======@xjx",destItem, content);
	HandleMessageQueue::getInstace()->pushMessage(destItem,content);
	return JNI_TRUE;
}

}
