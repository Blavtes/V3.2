package com.togic.weboxlauncher.util;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.togic.launcher.util.image.CacheObject;
import com.togic.launcher.util.image.impl.ImageSDCardCache;
import com.togic.weboxlauncher.App;
import com.togic.weboxlauncher.http.MetroApi;
import com.togic.weboxlauncher.model.MJsonInfo;
import com.togic.weboxlauncher.model.MetroDate;
import com.togic.weboxlauncher.model.MetroItemDate;
import com.togic.weboxlauncher.model.Minfo;
import com.togic.weboxlauncher.model.Page;

public class MMetroParser {

    private static final String TAG = "MetroParser";

    public static final String FILE_METRO = getMetroFileName();
    
    public static int sVesion = -1;

    public interface MParserCallback {
        public void onPreloadSuccess(String cacheDir);

        public void onParseFinished(boolean firstRead, Page page);
        
        public void onParseFinishedN(MetroDate md);
    }
    
    public static final String initParse(final Context ctx)
    {
    	String str = CommonUtil.getStringFromCach(ctx, FILE_METRO);
    	
    	if(str == null)
    	{
    		Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! getStringFromAsset");
    	    str = CommonUtil.getStringFromAsset(ctx, FILE_METRO);
    	}
    	else
    	{
    	}
    	if(str == null)
    	{
    		return null;
    	}
    	else
    	{
    		Log.v("@ppp", "MetroDate json !!!!!!!!!!!!!! " + str);
    	}
    	
    	Gson gson = new Gson();

        Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! 1");


        Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! 11" + str);
        
        MetroDate md = gson.fromJson(str, MetroDate.class);

        Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! 2");
        sVesion = md.version;
        
        MJsonInfo mif = new MJsonInfo(md.toMinfos());
        Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! 3");
        str = gson.toJson(mif);

        Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!! " + str);
        
        return str;
    }

    public static final void parse(final Context ctx,
            final MParserCallback callback, ImageSDCardCache cache) {
    	MetroDate md = null;
        if (cache != null) {
            final MetroApi api = MetroApi.getInstance(ctx);
            String str = api.getString(ctx, FILE_METRO,true);
            Gson gson = new Gson();
            Log.v("@ppp", "MetroDate md !!!!!!!!!!!!!!");
            
            md = gson.fromJson(str, MetroDate.class);
            
            if(sVesion >= md.version)
            {
            	return;
            }
            else
            {
            	sVesion = md.version;
            	MJsonInfo mif = new MJsonInfo(md.toMinfos());
            	CommonUtil.writeCache(ctx, FILE_METRO, (new Gson()).toJson(mif), null);
            }
            if (preloadMetro(cache, md) && fixUrl(cache, md)) {

                callback.onPreloadSuccess(cache.getCacheFolder());
            }
        } else {
            // Don't notify UI update if preload failed.
            LogUtil.w(TAG, "sdcard cache is null");
            callback.onParseFinished(false, null);
            return;
        }

        //callback.onParseFinishedN(md);
    }

	public static String getInfos(Context ctx) {
		String infos = null;

		infos = CommonUtil.getStringFromCach(ctx, FILE_METRO);

		if (infos == null) {
			infos = CommonUtil.getStringFromAsset(ctx, FILE_METRO);
		}
		return infos;
	}

    private static final boolean fixUrl(ImageSDCardCache cache, MetroDate md) {

    	md.background = getDownloadResult(cache, md.background);

        for (MetroItemDate item : md.items) {
        	item.background = getDownloadResult(cache, item.background);
        	item.icon = getDownloadResult(cache, item.icon);
        	item.bottom_bg = getDownloadResult(cache, item.bottom_bg);
        }

        return true;
    }

    private static final String getDownloadResult(ImageSDCardCache cache,
            String url) {
        if (needDownload(url)) {
            final CacheObject<String> file = cache.getImmediately(cache
                    .getFileNameRule().getFileName(url));
            return file == null ? null : file.getData();
        } else {
            return url;
        }
    }

    private static final void notifyFinished(Object obj) {
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    private static final void waitForFinish(Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final boolean preloadMetro(ImageSDCardCache c, MetroDate md) {
        if (c == null) {
            return false;
        }

        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&& begin download metro: "
                + System.currentTimeMillis());

        final DownloadResult r = new DownloadResult();
        synchronized (r) {
        	
            downloadPage(c, r, md);
            LogUtil.v(TAG, "&&&&&&&&&&&&&& task count: " + r.getTaskCount());

            if (r.getTaskCount() > 0) {
                waitForFinish(r);
            }
        }

        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&& end download metro: "
                + System.currentTimeMillis());

        LogUtil.v(TAG, "&&&&&&&&&&&&&& error count: " + r.getErrorCount());
        return r.getErrorCount() == 0;
    }

    private static final void downloadPage(ImageSDCardCache c,
            DownloadResult l, MetroDate md) {

        download(c, l, md.background);
        for (MetroItemDate item : md.items) {
            downloadItemBitmap(c, l, item);
        }
    }
    

    private static final void downloadItemBitmap(ImageSDCardCache c,
            DownloadResult l, MetroItemDate item) {
        download(c, l, item.needDownloadUrls());
    }

    private static final void download(final ImageSDCardCache c,
            final DownloadResult l, final String[] urls) {
        if (urls != null && urls.length > 0) {
            for (String url : urls) {
                download(c, l, url);
            }
        }
    }

    private static final void download(ImageSDCardCache c, DownloadResult l,
            String url) {
        if (needDownload(url)) {
        	Log.v("@ppp","download " + url);
            LogUtil.v(TAG, "need download bitmap from: " + url);
            new ConcurrentImageDownloader(c, l).execute(url);
        }
    }

    private static final boolean needDownload(String str) {
        return !CommonUtil.isEmptyString(str) && !CommonUtil.isColorString(str)
                && !CommonUtil.isLocalPath(str) && !CommonUtil.isAssetFile(str);
    }

    static final class DownloadResult {
        volatile int taskCount;
        volatile int errorCount;

        void addTask() {
            taskCount++;
        }

        void delTask() {
            taskCount--;
        }

        void addError() {
            errorCount++;
        }

        void checkTask() {
            if (taskCount <= 0) {
                notifyFinished(this);
            }
        }

        int getTaskCount() {
            return taskCount;
        }

        int getErrorCount() {
            return errorCount;
        }
    }

    static final class ConcurrentImageDownloader extends
            AsyncTask<String, Void, Boolean> {
        private static final int MAX_RETRY = 3;

        private final ImageSDCardCache cache;
        private final DownloadResult result;

        ConcurrentImageDownloader(ImageSDCardCache c, DownloadResult r) {
            cache = c;
            result = r;
        }

        @Override
        protected void onPreExecute() {
            result.addTask();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            final Options opts = new Options();
            opts.inSampleSize = 4;
            Bitmap b = null;

            final String url = params[0];
            final String key = cache.getFileNameRule().getFileName(url);
            CacheObject<String> obj = cache.getImmediately(key);
            if (obj != null) {
                if (!CommonUtil.isEmptyString(obj.getData())) {
                    b = retryDecode(obj.getData(), opts);
                }

                if (b != null) {
                    b.recycle();

                    changeLastModified(obj.getData());
                    return true;
                } else {
                    cache.remove(key);
                }
            }

            LogUtil.v(TAG, "load bitmap from: " + params);
            obj = cache.get(url);
            if (obj == null) {
                return false;
            } else if (CommonUtil.isEmptyString(obj.getData())) {
                cache.remove(url);
                return false;
            }

            b = retryDecode(obj.getData(), opts);
            if (b != null) {
                LogUtil.v(TAG, "load bitmap from: " + params + " success");
                b.recycle();

                changeLastModified(obj.getData());
                cache.removeButNotDeleteFile(url);
                cache.put(key, obj);
                return true;
            } else {
                cache.remove(key);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            result.delTask();
            if (!b) {
                result.addError();
            }

            result.checkTask();
        }

        private Bitmap retryDecode(String path, Options opts) {
            Bitmap b = null;
            for (int i = 0; i < MAX_RETRY; i++) {
                b = BitmapFactory.decodeFile(path, opts);
                if (b != null) {
                    return b;
                } else {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        private void changeLastModified(String path) {
            final File f = new File(path);
            if (f.exists()) {
                f.setLastModified(System.currentTimeMillis());
            }
        }
    }

    private static final String getMetroFileName() {
        if (App.sBetaVersion) {
            return "webox_launcher_metro_beta.json";
        } else {
            return "webox_launcher_metro.json";
        }
    }
}
