package com.togic.launcher.util.image.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.togic.launcher.util.image.CacheFullRemoveType;
import com.togic.launcher.util.image.CacheObject;
import com.togic.launcher.util.image.util.ImageUtils;
import com.togic.launcher.util.image.util.SizeUtils;
import com.togic.launcher.util.image.util.StringUtils;
import com.togic.launcher.util.image.util.SystemUtils;

/**
 * <strong>Image Memory Cache</strong><br/>
 * <br/>
 * It applies to images those uesd frequently, like users avatar of twitter or sina weibo. Cache of big image you can
 * consider of {@link ImageSDCardCache}.<br/>
 * <ul>
 * <strong>Setting and Usage</strong>
 * <li>Use one of constructors below to init cache</li>
 * <li>{@link #setOnImageCallbackListener(OnImageCallbackListener)} set callback interface after image get success</li>
 * <li>{@link #get(String, List, View)} get image asynchronous and preload other images asynchronous according to
 * urlList</li>
 * <li>{@link #get(String, View)} get image asynchronous</li>
 * <li>{@link #setHttpReadTimeOut(int)} set http read image time out, if less than 0, not set. default is not set</li>
 * <li>{@link #setOpenWaitingQueue(boolean)} set whether open waiting queue, default is true. If true, save all view
 * waiting for image loaded, else only save the newest one</li>
 * <li>{@link PreloadDataCache#setOnGetDataListener(OnGetDataListener)} set how to get image, this cache will get image
 * and preload images by it</li>
 * <li>{@link SimpleCache#setCacheFullRemoveType(CacheFullRemoveType)} set remove type when cache is full</li>
 * <li>other see {@link PreloadDataCache} and {@link SimpleCache}</li>
 * </ul>
 * <ul>
 * <strong>Constructor</strong>
 * <li>{@link #ImageCache()}</li>
 * <li>{@link #ImageCache(int)}</li>
 * <li>{@link #ImageCache(int, int)}</li>
 * </ul>
 * 
 * @author Trinea 2012-4-5
 */
public class ImageCache extends PreloadDataCache<String, Bitmap> {

    private static final long                    serialVersionUID   = 1L;

    private static final String                  TAG                = "ImageCache";

    /** callback interface after image get success **/
    private OnImageCallbackListener              onImageCallbackListener;
    /** http read image time out, if less than 0, not set. default is not set **/
    private int                                  httpReadTimeOut    = -1;
    /**
     * whether open waiting queue, default is true. If true, save all view waiting for image loaded, else only save the
     * newest one
     **/
    private boolean                              isOpenWaitingQueue = true;

    /** recommend default max cache size according to dalvik max memory **/
    public static final int                      DEFAULT_MAX_SIZE   = getDefaultMaxSize();
    /** image got success message what **/
    private static final int                     IMAGE_LOADED_WHAT  = 1;

    public static final int                      MAX_IMAGE_LOAD_TRY = 3;

    /** thread pool whose wait for data got, attention, not the get data thread pool **/
    private transient ExecutorService            threadPool         = Executors.newFixedThreadPool(SystemUtils.DEFAULT_THREAD_POOL_SIZE);
    /**
     * key is image url, value is the newest view which waiting for image loaded, used when {@link #isOpenWaitingQueue}
     * is false
     **/
    private transient Map<String, View>          viewMap;
    /**
     * key is image url, value is view set those waiting for image loaded, used when {@link #isOpenWaitingQueue} is true
     **/
    private transient Map<String, HashSet<View>> viewSetMap;

    private transient Handler                    handler;

    /**
     * get image asynchronous. when get image success, it will pass to
     * {@link OnImageCallbackListener#onImageLoaded(String, Drawable, View, boolean)}
     * 
     * @param imageUrl
     * @param view
     * @return whether image already in cache or not
     */
    public boolean get(String imageUrl, View view) {
        return get(imageUrl, null, view);
    }

    /**
     * get image asynchronous and preload other images asynchronous according to urlList
     * 
     * @param imageUrl
     * @param urlList url list, if is null, not preload, else preload forward by
     * {@link PreloadDataCache#preloadDataForward(Object, List, int)}, preload backward by
     * {@link PreloadDataCache#preloadDataBackward(Object, List, int)}
     * @param view
     * @return whether image already in cache or not
     */
    public boolean get(final String imageUrl, final List<String> urlList, final View view) {
        if (StringUtils.isEmpty(imageUrl)) {
            return false;
        }

        /**
         * if already in cache, call onImageSDCallbackListener, else new thread to wait for it
         */
        CacheObject<Bitmap> object = getFromCache(imageUrl, urlList);
        if (object != null) {
            Bitmap bm = object.getData();
            if (bm != null) {
                if (onImageCallbackListener != null) {
                    onImageCallbackListener.onImageLoaded(imageUrl, bm, view, true);
                }
                return true;
            } else {
                remove(imageUrl);
            }
        }

        if (isOpenWaitingQueue) {
            synchronized (viewSetMap) {
                HashSet<View> viewSet = viewSetMap.get(imageUrl);
                if (viewSet == null) {
                    viewSet = new HashSet<View>();
                    viewSetMap.put(imageUrl, viewSet);
                }
                viewSet.add(view);
            }
        } else {
            viewMap.put(imageUrl, view);
        }

        startGetImageThread(IMAGE_LOADED_WHAT, imageUrl, urlList);
        return false;
    }

    /**
     * get callback interface after image get success
     * 
     * @return the onImageCallbackListener
     */
    public OnImageCallbackListener getOnImageCallbackListener() {
        return onImageCallbackListener;
    }

    /**
     * set callback interface after image get success
     * 
     * @param onImageCallbackListener
     */
    public void setOnImageCallbackListener(OnImageCallbackListener onImageCallbackListener) {
        this.onImageCallbackListener = onImageCallbackListener;
    }

    /**
     * get http read image time out, if less than 0, not set. default is not set
     * 
     * @return the httpReadTimeOut
     */
    public int getHttpReadTimeOut() {
        return httpReadTimeOut;
    }

    /**
     * set http read image time out, if less than 0, not set. default is not set, in mills
     * 
     * @param readTimeOutMillis
     */
    public void setHttpReadTimeOut(int readTimeOutMillis) {
        this.httpReadTimeOut = readTimeOutMillis;
    }

    /**
     * get whether open waiting queue, default is true. If true, save all view waiting for image loaded, else only save
     * the newest one
     * 
     * @return
     */
    public boolean isOpenWaitingQueue() {
        return isOpenWaitingQueue;
    }

    /**
     * set whether open waiting queue, default is true. If true, save all view waiting for image loaded, else only save
     * the newest one
     * 
     * @param isOpenWaitingQueue
     */
    public void setOpenWaitingQueue(boolean isOpenWaitingQueue) {
        this.isOpenWaitingQueue = isOpenWaitingQueue;
    }

    /**
     * <ul>
     * <li>Get data listener is {@link #getDefaultOnGetImageListener()}</li>
     * <li>Callback interface after image get success is null, can set by
     * {@link #setOnImageCallbackListener(OnImageCallbackListener)}</li>
     * <li>Maximum size of the cache is {@link #DEFAULT_MAX_SIZE}</li>
     * <li>Elements of the cache will not invalid</li>
     * <li>Remove type is {@link RemoveTypeUsedCountSmall} when cache is full</li>
     * </ul>
     * 
     * @see PreloadDataCache#PreloadDataCache()
     */
    public ImageCache(){
        this(DEFAULT_MAX_SIZE, DEFAULT_MAX_BYTE_SIZE, PreloadDataCache.DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * <ul>
     * <li>Get data listener is {@link #getDefaultOnGetImageListener()}</li>
     * <li>Callback interface after image get success is null, can set by
     * {@link #setOnImageCallbackListener(OnImageCallbackListener)}</li>
     * <li>Elements of the cache will not invalid</li>
     * <li>Remove type is {@link RemoveTypeUsedCountSmall} when cache is full</li>
     * </ul>
     * 
     * @param maxSize maximum size of the cache
     * @see PreloadDataCache#PreloadDataCache(int)
     */
    public ImageCache(int maxSize, int maxByteSize){
        this(maxSize, maxByteSize, PreloadDataCache.DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * <ul>
     * <li>Get data listener is {@link #getDefaultOnGetImageListener()}</li>
     * <li>Callback interface after image get success is null, can set by
     * {@link #setOnImageCallbackListener(OnImageCallbackListener)}</li>
     * <li>Elements of the cache will not invalid</li>
     * <li>Remove type is {@link RemoveTypeUsedCountSmall} when cache is full</li>
     * </ul>
     * 
     * @param maxSize maximum size of the cache
     * @param threadPoolSize getting data thread pool size
     * @see PreloadDataCache#PreloadDataCache(int, int)
     */
    public ImageCache(int maxItemSize, int maxByteSize, int threadPoolSize){
        super(maxItemSize, maxByteSize, threadPoolSize);

        super.setOnGetDataListener(getDefaultOnGetImageListener());
        this.viewMap = new ConcurrentHashMap<String, View>();
        this.viewSetMap = new HashMap<String, HashSet<View>>();
        this.handler = new MyHandler();
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }

    /**
     * callback interface after image get success
     * 
     * @author Trinea 2012-4-5
     */
    public interface OnImageCallbackListener extends Serializable {

        /**
         * callback function after image get success, run on ui thread
         * 
         * @param imageUrl imageUrl
         * @param imageDrawable drawable
         * @param view view need the image
         * @param isInCache whether already in cache or got realtime
         */
        public void onImageLoaded(String imageUrl, Bitmap bitmap, View view, boolean isInCache);
    }

    /**
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        threadPool.shutdown();
        super.shutdown();
    }

    /**
     * @see ExecutorService#shutdownNow()
     */
    public List<Runnable> shutdownNow() {
        threadPool.shutdownNow();
        return super.shutdownNow();
    }

    /**
     * My handler
     * 
     * @author Trinea 2012-11-20
     */
    private class MyHandler extends Handler {

        public void handleMessage(Message message) {
            switch (message.what) {
                case IMAGE_LOADED_WHAT:
                    MessageObject object = (MessageObject)message.obj;
                    if (object == null) {
                        break;
                    }

                    String imageUrl = object.imageUrl;
                    Bitmap bm = object.bm;
                    if (onImageCallbackListener != null) {
                        if (isOpenWaitingQueue) {
                            synchronized (viewSetMap) {
                                HashSet<View> viewSet = viewSetMap.get(imageUrl);
                                if (viewSet != null) {
                                    for (View view : viewSet) {
                                        if (view != null) {
                                            onImageCallbackListener.onImageLoaded(imageUrl, bm, view, false);
                                        }
                                    }
                                }
                            }
                        } else {
                            View view = viewMap.get(imageUrl);
                            if (view != null) {
                                onImageCallbackListener.onImageLoaded(imageUrl, bm, view, false);
                            }
                        }
                    }

                    if (isOpenWaitingQueue) {
                        synchronized (viewSetMap) {
                            viewSetMap.remove(imageUrl);
                        }
                    } else {
                        viewMap.remove(imageUrl);
                    }
                    break;
            }
        }
    };

    /**
     * message object
     * 
     * @author Trinea 2013-1-14
     */
    private class MessageObject {

        String   imageUrl;
        Bitmap   bm;

        public MessageObject(String imageUrl, Bitmap bm){
            this.imageUrl = imageUrl;
            this.bm = bm;
        }
    }

    /**
     * start thread to wait for image get
     * 
     * @param messsageWhat
     * @param imageUrl
     * @param urlList url list, if is null, not preload, else preload forward by
     * {@link PreloadDataCache#preloadDataForward(Object, List, int)}, preload backward by
     * {@link PreloadDataCache#preloadDataBackward(Object, List, int)}
     */
    private void startGetImageThread(final int messsageWhat, final String imageUrl, final List<String> urlList) {
        // wait for image be got success and send message
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                CacheObject<Bitmap> object = get(imageUrl, urlList);
                Bitmap bm = (object == null ? null : object.getData());
                // if drawable is null, remove it
                if (bm == null) {
                    remove(imageUrl);
                } else {
                    handler.sendMessage(handler.obtainMessage(IMAGE_LOADED_WHAT, new MessageObject(imageUrl, bm)));
                }
            }
        });
    }

    /**
     * recycle bitmap when full remove one
     */
    @Override
    protected CacheObject<Bitmap> fullRemoveOne() {
        CacheObject<Bitmap> o = super.fullRemoveOne();
        if (o != null) {
            (o.getData()).recycle();
        }
        return o;
    }

    /**
     * Removes all elements from this Map, leaving it empty.
     *
     * @see Map#clear()
     */
    @Override
    public void clear() {
        Set<String> set = cache.keySet();
        for (String k : set) {
            ((cache.get(k)).getData()).recycle();
        }

        super.cache.clear();
    }

    /**
     * default get image listener
     * 
     * @return
     */
    public OnGetDataListener<String, Bitmap> getDefaultOnGetImageListener() {
        return new OnGetDataListener<String, Bitmap>() {

            private static final long serialVersionUID = 1L;

            @Override
            public CacheObject<Bitmap> onGetData(String key) {
                Bitmap bm = null;
                for (int i = 0; i < MAX_IMAGE_LOAD_TRY; i++) {
                    try {
                        bm = ImageUtils.getBitmapFromUrl(key, httpReadTimeOut);
                    } catch (Exception e) {
                        Log.e(TAG, "get drawable exception, imageUrl is:" + key, e);
                    }
                    if (bm != null) {
                        return new CacheObject<Bitmap>(bm);
                    }
                }
                return null;
            }
        };
    }

    /**
     * get recommend default max cache size according to dalvik max memory
     * 
     * @return
     */
    static int getDefaultMaxSize() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory > SizeUtils.GB_2_BYTE) {
            return 512;
        }

        int mb = (int)(maxMemory / SizeUtils.MB_2_BYTE);
        return mb > 16 ? mb * 2 : 16;
    }
}
