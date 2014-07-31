package com.togic.launcher.util.image.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.togic.launcher.util.image.CacheFullRemoveType;
import com.togic.launcher.util.image.CacheObject;
import com.togic.launcher.util.image.util.ListUtils;
import com.togic.launcher.util.image.util.ObjectUtils;
import com.togic.launcher.util.image.util.SerializeUtils;
import com.togic.launcher.util.image.util.SystemUtils;

/**
 * <strong>Preload data cache</strong>, It a good choice for network application which need to preload data.<br/>
 * <br/>
 * you can use this cache to preload data, it support preload data backward, forward or both. and you can set preload
 * count.<br/>
 * <ul>
 * <strong>Setting and Usage</strong>
 * <li>Use one of constructors below to init cache</li>
 * <li>{@link #setOnGetDataListener(OnGetDataListener)} set how to get data, this cache will get data and preload data
 * by it</li>
 * <li>{@link SimpleCache#setCacheFullRemoveType(CacheFullRemoveType)} set remove type when cache is full</li>
 * <li>{@link #get(Object, List)} get object, if list is not null, will preload data auto according to keys in list</li>
 * <li>{@link #get(Object)} get object, and not preload data</li>
 * <li>{@link #setForwardCacheNumber(int)} set count for preload forward, default is
 * {@link #DEFAULT_FORWARD_CACHE_NUMBER}</li>
 * <li>{@link #setBackwardCacheNumber(int)} set count for preload backward, default is
 * {@link #DEFAULT_BACKWARD_CACHE_NUMBER}</li>
 * <li>{@link #setContext(Context)} and {@link #setAllowedNetworkTypes(int)} restrict the types of networks over which
 * this data can get.</li>
 * <li>{@link SimpleCache#setValidTime(long)} set valid time of elements in cache, in mills</li>
 * <li>{@link SimpleCache#saveCache(String, SimpleCache)} save cache to a file</li>
 * </ul>
 * <ul>
 * <strong>Constructor</strong>
 * <li>{@link #PreloadDataCache()}</li>
 * <li>{@link #PreloadDataCache(int)}</li>
 * <li>{@link #PreloadDataCache(int, int)}</li>
 * <li>{@link #loadCache(String)} restore cache from file</li>
 * </ul>
 * 
 * @author Trinea 2012-3-4
 */
public class PreloadDataCache<K, V> extends SimpleCache<K, V> {

    private static final String             TAG                           = "PreloadDataCache";

    private static final long               serialVersionUID              = 1L;

    /** count for preload forward, default is {@link #DEFAULT_FORWARD_CACHE_NUMBER} **/
    private int                             forwardCacheNumber            = DEFAULT_FORWARD_CACHE_NUMBER;
    /** count for preload backward, default is {@link #DEFAULT_BACKWARD_CACHE_NUMBER} **/
    private int                             backwardCacheNumber           = DEFAULT_BACKWARD_CACHE_NUMBER;

    /** get data listener **/
    private OnGetDataListener<K, V>         onGetDataListener;

    /**
     * restore threads those getting data, to avoid multi threads get the data for same key so that to save network
     * traffic
     **/
    private transient Map<K, GetDataThread> gettingDataThreadMap          = new HashMap<K, GetDataThread>();

    /** getting data thread pool **/
    private ExecutorService                 threadPool;

    /** default count for preload forward **/
    public static final int                 DEFAULT_FORWARD_CACHE_NUMBER  = 3;
    /** default count for preload backward **/
    public static final int                 DEFAULT_BACKWARD_CACHE_NUMBER = 1;

    /** default getting data thread pool size **/
    public static final int                 DEFAULT_THREAD_POOL_SIZE      = SystemUtils.getDefaultThreadPoolSize(8);

    /**
     * Bit flag for {@link #setAllowedNetworkTypes} corresponding to {@link ConnectivityManager#TYPE_MOBILE}.
     */
    public static final int                 NETWORK_MOBILE                = 1 << 0;
    /**
     * Bit flag for {@link #setAllowedNetworkTypes} corresponding to {@link ConnectivityManager#TYPE_WIFI}.
     */
    public static final int                 NETWORK_WIFI                  = 1 << 1;


    public CacheObject<V> getImmediately(K key) {
        if (key == null) {
            return null;
        }
        CacheObject<V> object = super.get(key);
        if (object != null) {
            object.setEnterTime(System.currentTimeMillis());
        }
        return object;
    }

    /**
     * get data synchronous and preload new data asynchronous according to keyList
     * 
     * @param key
     * @param keyList key list, if is null, not preload, else preload forward by
     * {@link #preloadDataForward(Object, List, int)}, preload backward by
     * {@link #preloadDataBackward(Object, List, int)}
     * @return element if this cache contains the specified key, else get data realtime and wait for it
     * @see {@link #get(Object)}
     */
    public CacheObject<V> get(K key, List<K> keyList) {
        if (key == null) {
            return null;
        }

        // if list is not null, preload data
        if (!ListUtils.isEmpty(keyList)) {
            preloadDataForward(key, keyList, forwardCacheNumber);
        }

        return get(key);
    }

    /**
     * get data synchronous
     * <ul>
     * <li>if key is null, return null, else</li>
     * <li>if key is already in cache, return the element that mapping with the specified key, else</li>
     * <li>call {@link OnGetDataListener#onGetData(Object)} to get data and wait for it finish</li>
     * </ul>
     * 
     * @param key
     * @return element if this cache contains the specified key, else get data realtime and wait for it
     */
    @Override
    public CacheObject<V> get(K key) {
        if (key == null) {
            return null;
        }

        CacheObject<V> object = super.get(key);
        if (object == null && onGetDataListener != null) {
            GetDataThread getDataThread = gettingData(key);
            // get data synchronous and wait for it
            if (getDataThread != null) {
                try {
                    getDataThread.finishGetDataLock.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // recalculate hit rate
            object = super.get(key);
            if (object != null) {
                hitCount.decrementAndGet();
            } else {
                missCount.decrementAndGet();
            }
        }
        return object;
    }

    /**
     * get data from cache
     * 
     * @param key
     * @return element if this cache contains the specified key, null otherwise.
     */
    CacheObject<V> getFromCache(K key) {
        return super.get(key);
    }

    /**
     * get data from cache and preload new data asynchronous according to keyList
     * 
     * @param key
     * @param keyList key list, if is null, not preload, else preload forward by
     * {@link #preloadDataForward(Object, List, int)}, preload backward by
     * {@link #preloadDataBackward(Object, List, int)}
     * @return element if this cache contains the specified key, null otherwise.
     * @see {@link #getFromCache(Object)}
     */
    CacheObject<V> getFromCache(K key, List<K> keyList) {
        if (key == null) {
            return null;
        }

        // if list is not null, preload data
        if (!ListUtils.isEmpty(keyList)) {
            preloadDataForward(key, keyList, forwardCacheNumber);
        }

        return getFromCache(key);
    }

    /**
     * preload data forward
     * <ul>
     * <strong>Preload rule below:</strong><br/>
     * If key is null or list is empty, not preload, else circle keyList front to back.<br/>
     * If entry in the list equals to key, begin preload until to the end of list or preload count has reached
     * cacheCount, like this:
     * <li>if entry is already in cache or is getting data, continue next entry. else</li>
     * <li>new thread to get data and continue next entry</li>
     * </ul>
     * 
     * @param key
     * @param keyList if is null, not preload
     * @param cacheCount count for preload forward
     * @return count for getting data, that is cacheCount minus count of keys whose alreadey in cache
     */
    protected int preloadDataForward(K key, List<K> keyList, int cacheCount) {
        int gettingDataCount = 0;
        if (key != null && !ListUtils.isEmpty(keyList) && onGetDataListener != null) {
            int cachedCount = 0;
            boolean beginCount = false;
            for (int i = 0; i < keyList.size() && cachedCount < cacheCount; i++) {
                K k = keyList.get(i);
                if (ObjectUtils.isEquals(k, key)) {
                    beginCount = true;
                    continue;
                }

                if (k == null || cache.containsKey(k)) {
                    cachedCount++;
                    continue;
                }
                if (k != null && beginCount) {
                    cachedCount++;
                    if (gettingData(k) != null) {
                        gettingDataCount++;
                    }
                }
            }
        }
        return gettingDataCount;
    }

    /**
     * preload data backward
     * <ul>
     * <strong>Preload rule below:</strong><br/>
     * If key is null or list is empty, not preload, else circle keyList back to front.<br/>
     * If entry in the list equals to key, begin preload until to the front of list or preload count has reached
     * cacheCount, like this:
     * <li>if entry is already in cache or is getting data, continue last entry. else</li>
     * <li>new thread to get data and continue last entry</li>
     * </ul>
     * 
     * @param key
     * @param keyList if is null, not preload
     * @param cacheCount count for preload forward
     * @return count for getting data, that is cacheCount minus count of keys whose alreadey in cache
     */
    protected int preloadDataBackward(K key, List<K> keyList, int cacheCount) {
        int gettingDataCount = 0;
        if (key != null && !ListUtils.isEmpty(keyList) && onGetDataListener != null) {
            int cachedCount = 0;
            boolean beginCount = false;
            for (int i = keyList.size() - 1; i >= 0 && cachedCount < cacheCount; i--) {
                K k = keyList.get(i);
                if (ObjectUtils.isEquals(k, key)) {
                    beginCount = true;
                    continue;
                }

                if (k != null && beginCount) {
                    cachedCount++;
                    if (gettingData(k) != null) {
                        gettingDataCount++;
                    }
                }
            }
        }
        return gettingDataCount;
    }

    /**
     * get getting data thread
     * <ul>
     * <li>if key is already in cache or net work type is not allowed, return null, else</li>
     * <li>if there is a thread which is getting data for the specified key, return thread, else</li>
     * <li>new thread to get data and return it</li>
     * </ul>
     * 
     * @param key
     * @return
     */
    private synchronized GetDataThread gettingData(K key) {
        if (containsKey(key)) {
            return null;
        }

        if (isExistGettingDataThread(key)) {
            return gettingDataThreadMap.get(key);
        }

        GetDataThread getDataThread = new GetDataThread(key, onGetDataListener);
        gettingDataThreadMap.put(key, getDataThread);
        threadPool.execute(getDataThread);
        return getDataThread;
    }

    /**
     * whether there is a thread which is getting data for the specified key
     * 
     * @param key
     * @return
     */
    public synchronized boolean isExistGettingDataThread(K key) {
        return gettingDataThreadMap.containsKey(key);
    }

    /**
     * <ul>
     * <li>Maximum size of the cache is {@link SimpleCache#DEFAULT_MAX_SIZE}</li>
     * <li>Elements of the cache will not invalid, can set by {@link SimpleCache#setValidTime(long)}</li>
     * <li>Remove type is {@link RemoveTypeEnterTimeFirst} when cache is full</li>
     * <li>Size of getting data thread pool is {@link #DEFAULT_THREAD_POOL_SIZE}</li>
     * </ul>
     */
    public PreloadDataCache(){
        this(DEFAULT_MAX_ITEM_SIZE, DEFAULT_MAX_BYTE_SIZE, DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * <ul>
     * <li>Elements of the cache will not invalid, can set by {@link SimpleCache#setValidTime(long)}</li>
     * <li>Remove type is {@link RemoveTypeEnterTimeFirst} when cache is full</li>
     * <li>Size of getting data thread pool is {@link #DEFAULT_THREAD_POOL_SIZE}</li>
     * </ul>
     * 
     * @param maxSize maximum size of the cache
     */
    public PreloadDataCache(int maxSize){
        this(maxSize, DEFAULT_MAX_BYTE_SIZE, DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * <ul>
     * <li>Elements of the cache will not invalid, can set by {@link SimpleCache#setValidTime(long)}</li>
     * <li>Remove type is {@link RemoveTypeEnterTimeFirst} when cache is full</li>
     * </ul>
     * 
     * @param maxSize maximum size of the cache
     * @param threadPoolSize getting data thread pool size
     */
    public PreloadDataCache(int maxSize, long maxByteSize, int threadPoolSize){
        super(maxSize, maxByteSize);

        if (threadPoolSize <= 0) {
            throw new IllegalArgumentException("The threadPoolSize of cache must be greater than 0.");
        }
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * get count for preload forward, default is {@link #DEFAULT_FORWARD_CACHE_NUMBER}
     * 
     * @return
     */
    public int getForwardCacheNumber() {
        return forwardCacheNumber;
    }

    /**
     * set count for preload forward, default is {@link #DEFAULT_FORWARD_CACHE_NUMBER}
     * 
     * @param forwardCacheNumber
     */
    public void setForwardCacheNumber(int forwardCacheNumber) {
        this.forwardCacheNumber = forwardCacheNumber;
    }

    /**
     * get count for preload backward, default is {@link #DEFAULT_BACKWARD_CACHE_NUMBER}
     * 
     * @return
     */
    public int getBackwardCacheNumber() {
        return backwardCacheNumber;
    }

    /**
     * set count for preload backward, default is {@link #DEFAULT_BACKWARD_CACHE_NUMBER}
     * 
     * @param backwardCacheNumber
     */
    public void setBackwardCacheNumber(int backwardCacheNumber) {
        this.backwardCacheNumber = backwardCacheNumber;
    }

    /**
     * get get data listener
     * 
     * @return the onGetDataListener
     */
    public OnGetDataListener<K, V> getOnGetDataListener() {
        return onGetDataListener;
    }

    /**
     * set get data listener, this cache will get data and preload data by it
     * 
     * @param onGetDataListener
     */
    public void setOnGetDataListener(OnGetDataListener<K, V> onGetDataListener) {
        this.onGetDataListener = onGetDataListener;
    }

    /**
     * restore cache from file
     * 
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K, V> PreloadDataCache<K, V> loadCache(String filePath) {
        return (PreloadDataCache<K, V>)SerializeUtils.deserialization(filePath);
    }

    /**
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * @see ExecutorService#shutdownNow()
     */
    public List<Runnable> shutdownNow() {
        return threadPool.shutdownNow();
    }

    /**
     * get data interface, implements this to get data
     * 
     * @author Trinea 2012-3-4
     */
    public interface OnGetDataListener<K, V> extends Serializable {

        /**
         * get data
         * 
         * @param key
         * @return the data need to be cached
         */
        public CacheObject<V> onGetData(K key);
    }

    /**
     * the thread to get data
     * 
     * @author Trinea 2012-3-4
     */
    private class GetDataThread implements Runnable {

        private K                       key;
        private OnGetDataListener<K, V> onGetDataListener;

        /** get data and cache finish lock, it will be released then **/
        public CountDownLatch           finishGetDataLock;

        /**
         * @param key
         * @param onGetDataListener
         */
        public GetDataThread(K key, OnGetDataListener<K, V> onGetDataListener){
            this.key = key;
            this.onGetDataListener = onGetDataListener;
            finishGetDataLock = new CountDownLatch(1);
        }

        public void run() {
            if (key != null && onGetDataListener != null) {
                CacheObject<V> object = onGetDataListener.onGetData(key);
                if (object != null) {
                    put(key, object);
                }
            }
            // get data success, release lock
            finishGetDataLock.countDown();

            if (gettingDataThreadMap != null && key != null) {
                gettingDataThreadMap.remove(key);
            }
        }
    };
}
