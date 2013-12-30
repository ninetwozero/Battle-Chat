package com.ninetwozero.battlechat.network;

import com.github.kevinsawicki.http.HttpRequest;
import com.ninetwozero.battlechat.datatypes.Session;

import java.net.URL;

public class SimpleGetRequest extends BaseSimpleRequest {
    @Deprecated
    public SimpleGetRequest(final String requestUrl) {
        super(requestUrl);
    }

    public SimpleGetRequest(final URL requestUrl) {
        super(requestUrl);
    }

    protected HttpRequest getHttpRequest() {
        final HttpRequest request = HttpRequest.get(requestUrl)
            .readTimeout(READ_TIMEOUT)
            .connectTimeout(CONNECT_TIMEOUT)
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie", Session.getCookieName() + "=" + Session.getCookieValue());
        return request;
    }
}