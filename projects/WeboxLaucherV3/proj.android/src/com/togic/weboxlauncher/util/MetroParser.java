///**
// * Copyright (C) 2012 Togic Corporation. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.togic.weboxlauncher.util;
//
//import java.io.File;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BitmapFactory.Options;
//import android.os.AsyncTask;
//
//import com.togic.launcher.util.image.CacheObject;
//import com.togic.launcher.util.image.impl.ImageSDCardCache;
//import com.togic.remote.error.RemoteError;
//import com.togic.remote.error.RemoteParseException;
//import com.togic.weboxlauncher.App;
//import com.togic.weboxlauncher.http.MetroApi;
//import com.togic.weboxlauncher.model.ItemData;
//import com.togic.weboxlauncher.model.Page;
//import com.togic.weboxlauncher.model.PageParser;
//
///**
// * @author mountains.liu@togic.com @date 2014-03-10
// */
//public class MetroParser {
//
//    private static final String TAG = "MetroParser";
//
//    public static final String FILE_METRO = getMetroFileName();
//
//    public interface ParserCallback {
//        public void onPreloadSuccess(String cacheDir);
//
//        public void onParseFinished(boolean firstRead, Page page);
//    }
//
//    private static long sVersion;
//
//    public static final void parse(final Context ctx,
//            final ParserCallback callback, ImageSDCardCache cache,
//            boolean firstRead,
//            boolean cibnResult) {
//        LogUtil.v(TAG, "************** first read metro: " + firstRead);
//
//        Page page = null;
//        if (firstRead) {
//            if (CommonUtil.isEqualsString(FILE_METRO,
//                    PreferenceUtil.getMetroFileName(ctx))) {
//                LogUtil.d(TAG, "read cache metro");
//                page = PreferenceUtil.getMetro(ctx);
//            } else {
//                // clear cache and update metro file name
//                LogUtil.w(TAG,
//                        "metro file name has changed, so clear cache metro");
//                PreferenceUtil.setMetro(ctx, null);
//                PreferenceUtil.setMetroFileName(ctx, FILE_METRO);
//            }
//
//            if (page == null || !page.isValid()) {
//                page = parse(CommonUtil.getStringFromAsset(ctx, FILE_METRO));
//            }
//
//            sVersion = page.version;
//        } else if (cache != null) {
//            final MetroApi api = MetroApi.getInstance(ctx);
//            page = parse(api.getString(ctx, FILE_METRO,cibnResult));
//
//            if (page == null || !page.isValid() || sVersion >= page.version) {
//                callback.onParseFinished(firstRead, null);
//                return;
//            }
//
//            if (preloadMetro(cache, page) && fixUrl(cache, page)) {
//                PreferenceUtil.setMetro(ctx, page);
//                sVersion = page.version;
//
//                callback.onPreloadSuccess(cache.getCacheFolder());
//            } else {
//                // Don't notify UI update if preload failed.
//                callback.onParseFinished(firstRead, null);
//                return;
//            }
//        } else {
//            // Don't notify UI update if preload failed.
//            LogUtil.w(TAG, "sdcard cache is null");
//            callback.onParseFinished(firstRead, null);
//            return;
//        }
//
//        callback.onParseFinished(firstRead, page);
//    }
//
//	public static String getInfos(Context ctx) {
//		String infos = null;
//
//		infos = CommonUtil.getStringFromCach(ctx, FILE_METRO);
//
//		if (infos == null) {
//			infos = CommonUtil.getStringFromAsset(ctx, FILE_METRO);
//		}
//		return infos;
//	}
//
//    private static final Page parse(String json) {
//        try {
//            if (!CommonUtil.isEmptyString(json)) {
//                return new PageParser().parse(new JSONObject(json));
//            }
//        } catch (RemoteError e) {
//            e.printStackTrace();
//        } catch (RemoteParseException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private static final boolean fixUrl(ImageSDCardCache cache, Page page) {
//        boolean result = page.fixDownloadUrls(getDownloadResults(cache,
//                page.needDownloadUrls()));
//        if (!result) {
//            LogUtil.i(TAG, "can't fix urls of Page: " + page);
//            return false;
//        }
//
//        for (ItemData item : page.getItems()) {
//            result = item.fixDownloadUrls(getDownloadResults(cache,
//                    item.needDownloadUrls()));
//
//            if (!result) {
//                LogUtil.i(TAG, "can't fix urls of Item: " + item);
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private static final String[] getDownloadResults(ImageSDCardCache cache,
//            String[] urls) {
//        final int length = CommonUtil.getArrayLength(urls);
//        for (int i = 0; i < length; i++) {
//            urls[i] = getDownloadResult(cache, urls[i]);
//        }
//
//        return urls;
//    }
//
//    private static final String getDownloadResult(ImageSDCardCache cache,
//            String url) {
//        if (needDownload(url)) {
//            final CacheObject<String> file = cache.getImmediately(cache
//                    .getFileNameRule().getFileName(url));
//            return file == null ? null : file.getData();
//        } else {
//            return url;
//        }
//    }
//
//    private static final void notifyFinished(Object obj) {
//        synchronized (obj) {
//            obj.notifyAll();
//        }
//    }
//
//    private static final void waitForFinish(Object obj) {
//        synchronized (obj) {
//            try {
//                obj.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static final boolean preloadMetro(ImageSDCardCache c, Page page) {
//        if (c == null) {
//            return false;
//        }
//
//        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&& begin download metro: "
//                + System.currentTimeMillis());
//
//        final DownloadResult r = new DownloadResult();
//        synchronized (r) {
//            downloadPage(c, r, page);
//            LogUtil.v(TAG, "&&&&&&&&&&&&&& task count: " + r.getTaskCount());
//
//            if (r.getTaskCount() > 0) {
//                waitForFinish(r);
//            }
//        }
//
//        LogUtil.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&& end download metro: "
//                + System.currentTimeMillis());
//
//        LogUtil.v(TAG, "&&&&&&&&&&&&&& error count: " + r.getErrorCount());
//        return r.getErrorCount() == 0;
//    }
//
//    private static final void downloadPage(ImageSDCardCache c,
//            DownloadResult l, Page page) {
//        if (page == null || !page.isValid()) {
//            LogUtil.w(TAG, "invalid pages data, no need download bitmap.");
//            return;
//        }
//
//        download(c, l, page.needDownloadUrls());
//        for (ItemData item : page.getItems()) {
//            downloadItemBitmap(c, l, item);
//        }
//    }
//
//    private static final void downloadItemBitmap(ImageSDCardCache c,
//            DownloadResult l, ItemData item) {
//        download(c, l, item.needDownloadUrls());
//    }
//
//    private static final void download(final ImageSDCardCache c,
//            final DownloadResult l, final String[] urls) {
//        if (urls != null && urls.length > 0) {
//            for (String url : urls) {
//                download(c, l, url);
//            }
//        }
//    }
//
//    private static final void download(ImageSDCardCache c, DownloadResult l,
//            String url) {
//        if (needDownload(url)) {
//            LogUtil.v(TAG, "need download bitmap from: " + url);
//            new ConcurrentImageDownloader(c, l).execute(url);
//        }
//    }
//
//    private static final boolean needDownload(String str) {
//        return !CommonUtil.isEmptyString(str) && !CommonUtil.isColorString(str)
//                && !CommonUtil.isLocalPath(str) && !CommonUtil.isAssetFile(str);
//    }
//
//    static final class DownloadResult {
//        volatile int taskCount;
//        volatile int errorCount;
//
//        void addTask() {
//            taskCount++;
//        }
//
//        void delTask() {
//            taskCount--;
//        }
//
//        void addError() {
//            errorCount++;
//        }
//
//        void checkTask() {
//            if (taskCount <= 0) {
//                notifyFinished(this);
//            }
//        }
//
//        int getTaskCount() {
//            return taskCount;
//        }
//
//        int getErrorCount() {
//            return errorCount;
//        }
//    }
//
//    static final class ConcurrentImageDownloader extends
//            AsyncTask<String, Void, Boolean> {
//        private static final int MAX_RETRY = 3;
//
//        private final ImageSDCardCache cache;
//        private final DownloadResult result;
//
//        ConcurrentImageDownloader(ImageSDCardCache c, DownloadResult r) {
//            cache = c;
//            result = r;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            result.addTask();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            final Options opts = new Options();
//            opts.inSampleSize = 4;
//            Bitmap b = null;
//
//            final String url = params[0];
//            final String key = cache.getFileNameRule().getFileName(url);
//            CacheObject<String> obj = cache.getImmediately(key);
//            if (obj != null) {
//                if (!CommonUtil.isEmptyString(obj.getData())) {
//                    b = retryDecode(obj.getData(), opts);
//                }
//
//                if (b != null) {
//                    b.recycle();
//
//                    changeLastModified(obj.getData());
//                    return true;
//                } else {
//                    cache.remove(key);
//                }
//            }
//
//            LogUtil.v(TAG, "load bitmap from: " + params);
//            obj = cache.get(url);
//            if (obj == null) {
//                return false;
//            } else if (CommonUtil.isEmptyString(obj.getData())) {
//                cache.remove(url);
//                return false;
//            }
//
//            b = retryDecode(obj.getData(), opts);
//            if (b != null) {
//                LogUtil.v(TAG, "load bitmap from: " + params + " success");
//                b.recycle();
//
//                changeLastModified(obj.getData());
//                cache.removeButNotDeleteFile(url);
//                cache.put(key, obj);
//                return true;
//            } else {
//                cache.remove(key);
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean b) {
//            result.delTask();
//            if (!b) {
//                result.addError();
//            }
//
//            result.checkTask();
//        }
//
//        private Bitmap retryDecode(String path, Options opts) {
//            Bitmap b = null;
//            for (int i = 0; i < MAX_RETRY; i++) {
//                b = BitmapFactory.decodeFile(path, opts);
//                if (b != null) {
//                    return b;
//                } else {
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            return null;
//        }
//
//        private void changeLastModified(String path) {
//            final File f = new File(path);
//            if (f.exists()) {
//                f.setLastModified(System.currentTimeMillis());
//            }
//        }
//    }
//
//    private static final String getMetroFileName() {
//        if (App.sBetaVersion) {
//            return "webox_launcher_metro_beta.json";
//        } else {
//            return "webox_launcher_metro.json";
//        }
//    }
//}
