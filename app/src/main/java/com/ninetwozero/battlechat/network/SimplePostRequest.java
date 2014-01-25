package com.ninetwozero.battlechat.network;

import android.os.Bundle;

import com.github.kevinsawicki.http.HttpRequest;
import com.ninetwozero.battlechat.datatypes.Session;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SimplePostRequest extends BaseSimpleRequest {
    private final Bundle postData;

    public SimplePostRequest(final String requestUrl, final Bundle postData) {
        super(requestUrl);
        this.postData = postData;
    }

    public SimplePostRequest(final URL requestUrl, final Bundle postData) {
        super(requestUrl);
        this.postData = postData;
    }

    protected HttpRequest getHttpRequest() {
        return HttpRequest.post(requestUrl)
            .readTimeout(READ_TIMEOUT)
            .connectTimeout(CONNECT_TIMEOUT)
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie", Session.getCookieName() + "=" + Session.getCookieValue())
            .form(fetchBundleAsMap(postData));
    }

    private Map<String, String> fetchBundleAsMap(final Bundle postData) {
        final Map<String, String> map = new HashMap<String, String>();
        for (String key : postData.keySet()) {
            map.put(key, String.valueOf(postData.get(key)));
        }
        return map;
    }
}
