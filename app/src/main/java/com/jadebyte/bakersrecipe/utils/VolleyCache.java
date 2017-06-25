package com.jadebyte.bakersrecipe.utils;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

import java.lang.ref.WeakReference;
import java.util.Map;

public class VolleyCache {
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse strongResponse, long cacheDuration) {
        NetworkResponse weakResponse = new WeakReference<>(strongResponse).get(); // Just to be on the save side to keep the reference after it's no longer needed
        long now = System.currentTimeMillis();

        Map<String, String> headers = weakResponse.headers;
        long serverDate = 0;
        String serverEtag;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheDuration;

        Cache.Entry entry = new Cache.Entry();
        entry.data = weakResponse.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }
}
