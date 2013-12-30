package com.ninetwozero.battlechat.factories;

import org.apache.http.client.utils.URIUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlFactory {
    private static final String SCHEME = "http";
    private static final String HOST = "battlelog.battlefield.com/bf4";
    private static final int DEFAULT_PORT = -1;

    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/%s/?s=320&d=%s";
    private static final String DEFAULT_GRAVATAR = "http://battlelog-cdn.battlefield.com/cdnprefix/avatar1/public/base/shared/default-avatar-320.png";

    public static URL buildBaseUrl() {
        return createURL("");
    }

    public static String buildGravatarUrl(final String hash) {
        return String.format(GRAVATAR_URL, hash, DEFAULT_GRAVATAR);
    }

    public static String buildLoginUrl() {
        return "https://battlelog.battlefield.com/bf4/gate/login";
    }

    public static URL buildLogoutUrl() {
        return createURL("session/logout/");
    }

    public static URL buildFriendListURL() {
        return createURL("comcenter/initComcenter/");
    }

    public static URL buildOpenChatURL(final String userId) {
        return createURL(String.format("comcenter/getChatId/%s/", userId));
    }

    public static URL buildPostToChatURL() {
        return createURL("comcenter/sendChatMessage/");
    }

    public static URL buildCloseChatURL(final long chatId) {
        return createURL(String.format("comcenter/hideChat/%d/", chatId));
    }

    private static URL createURL(final String path) {
        return prepareURL(HOST, path, null);
    }

    private static URL prepareURL(final String host, final String path, final String query) {
        try {
            return URIUtils.createURI(SCHEME, host, DEFAULT_PORT, path, query, null).toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
