package com.togic.weboxlauncher;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
//import org.cocos2dx.lib.Cocos2dxVideoHelper;



import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class MCocos2dxGLSurfaceView  extends Cocos2dxGLSurfaceView{	
	public interface KeyCallBack {
        void keyEvnDown(int code);
    } 
	private KeyCallBack callback;
	private Context mContext;
	
	static int keyAL[] = 
			{
		    KeyEvent.KEYCODE_DPAD_UP,
			KeyEvent.KEYCODE_DPAD_LEFT,
			KeyEvent.KEYCODE_DPAD_DOWN,
			KeyEvent.KEYCODE_DPAD_RIGHT,
			KeyEvent.KEYCODE_VOLUME_UP,
			KeyEvent.KEYCODE_VOLUME_DOWN
			
			};
	public MCocos2dxGLSurfaceView(final Context context) {
		super(context);
		mContext = context;
	}
	
	public void regcallback(KeyCallBack cbk)
	{
		callback = cbk;
	}

	public MCocos2dxGLSurfaceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
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
		if(pKeyEvent.getAction() == KeyEvent.ACTION_DOWN)
		checkKey(pKeyCode);
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
			this.queueEvent(new Runnable() {
				@Override
				public void run() {
					nativeKeyEvent(pKeyCode, pKeyEvent.getAction());
				}
			});
			return true;
		default:
			return false;
	}
	}
	private native static boolean nativeKeyEvent(int pKeyCode, int pKeyAction);
	static int num = 0;
	private void checkKey(int code)
	{   
		if(code == KeyEvent.KEYCODE_DPAD_UP)
		{
	        num = 1;
		}
		else
		{
			if(code == keyAL[num%keyAL.length])
			{
				num++;
			}
			else
			{
				num = 0;
			}
		}

		if(num >= keyAL.length)
		{
	        if(callback != null)
	        	callback.keyEvnDown(0);
		}
	}
}
