package com.cootv.ni.utils;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;


import java.io.File;


public class Volley {

    /** Default on-disk cache directory. */
    public static final String DEFAULT_CACHE_DIR = "volley";


    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        return newRequestQueue(context, stack, new File(context.getCacheDir(), DEFAULT_CACHE_DIR));
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack, File cacheDir) {
        return newRequestQueue(context, stack, cacheDir, -1);
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack, File cacheDir, int diskCacheSize) {
        String userAgent = "volley/0";

        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {

            if (Build.VERSION.SDK_INT >= 17) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Log.e("en condicion  >= ","KITKAT");
                    // Use a socket factory that removes sslv3
                    stack = new HurlStack(null, new NoSSLv3Compat.NoSSLv3Factory());
                } else {
                    stack = new HurlStack();
                    Log.e("FUERA DE CONDICION ","KITKAT");
                }
            }/* else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }*/
        }



        Network network = new BasicNetwork(stack);

        DiskBasedCache diskCache = diskCacheSize < 0 ? new DiskBasedCache(cacheDir) : new DiskBasedCache(cacheDir, diskCacheSize);
        RequestQueue queue = new RequestQueue(diskCache, network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

}