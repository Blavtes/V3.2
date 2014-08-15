package com.togic.weboxlauncher;

import org.cocos2dx.lib.Cocos2dxRenderer;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
//import org.cocos2dx.lib.Cocos2dxVideoHelper;



import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class MCocos2dxGLSurfaceView  extends Cocos2dxGLSurfaceView{	
	public MCocos2dxGLSurfaceView(final Context context) {
		super(context);
	}

	public MCocos2dxGLSurfaceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onKeyDown(int pKeyCode, KeyEvent pKeyEvent) {
		// TODO Auto-generated method stub
		if(onDispatchKeyEvent(pKeyCode, pKeyEvent))
		{
			return true;
		}
		return super.onKeyDown(pKeyCode, pKeyEvent);
	}
	
	@Override
	public boolean onKeyUp(int pKeyCode, KeyEvent pKeyEvent) {
		// TODO Auto-generated method stub
		if(onDispatchKeyEvent(pKeyCode, pKeyEvent))
		{
			return true;
		}
		return super.onKeyUp(pKeyCode, pKeyEvent);
	}
	
	private boolean onDispatchKeyEvent(final int pKeyCode, final KeyEvent pKeyEvent)
	{
		switch (pKeyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		case KeyEvent.KEYCODE_DPAD_CENTER:
//			nativeKeyEvent(pKeyCode, pKeyEvent.getAction());
			this.queueEvent(new Runnable() {
				@Override
				public void run() {
//					nativeKeyEvent(pKeyCode, pKeyEvent.getAction());

					nativeKeyEvent(pKeyCode, pKeyEvent.getAction());
				}
			});
			return true;
		default:
			return false;
	}
	}
	private native static boolean nativeKeyEvent(int pKeyCode, int pKeyAction);
}
