/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

package com.ninetwozero.battlechat.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class LoginHtmlParser {
    public static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/%s/?s=320&d=%s";
    public static final String DEFAULT_GRAVATAR = "http://battlelog-cdn.battlefield.com/cdnprefix/avatar1/public/base/shared/default-avatar-320.png";
    public static final String USER_AGENT_CHROME = " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.151 Safari/535.19";

    private final Document document;

    public LoginHtmlParser(final String html) {
        document = Jsoup.parse(html);
    }

    public LoginHtmlParser(final Document document) {
        this.document = document.clone();
    }

    public String getUserId() {
        return document.select("#base-header-user-tools .avatar").attr("rel");
    }

    public String getUsername() {
        return document.select("#base-header-user-tools .header-profile-dropdown .profile > a > div").text();
    }

    public String getChecksum() {
        return document.select("#profile-edit-form input[name=post-check-sum]").val();
    }

    public String getGravatarUrl() {
        final String style = document.select("#base-header-user-tools .avatar").attr("style");
        final Pattern pattern = Pattern.compile(".*/avatar/(\\w+)\\?.*");
        final Matcher matcher = pattern.matcher(style);

        return matcher.matches() ? getFullGravatarUrl(matcher.group(1)) : DEFAULT_GRAVATAR;
    }

    public boolean hasErrorMessage() {
        final Elements error = document.select(".gate-login-errormsg.wfont");
        return !error.isEmpty() && error.hasText();
    }

    public String getErrorMessage() {
        return document.select(".gate-login-errormsg.wfont").first().text();
    }

    public boolean isLoggedIn() {
        return document.select(".gate-login-errormsg").isEmpty();
    }

    public static String getFullGravatarUrl(final String hash) {
        return String.format(GRAVATAR_URL, hash, DEFAULT_GRAVATAR);
    }

    @Override
    public String toString() {
        return "LoginHtmlParser{" +
            ", userId=" + getUserId() +
            ", username='" + getUsername() + '\'' +
            ", checksum='" + getChecksum() + '\'' +
            ", gravatar='" + getGravatarUrl() + '\'' +
            ", errorMessage=" + (hasErrorMessage() ? getErrorMessage() : "N/A") + '\'' +
            ", loggedIn=" + isLoggedIn() +
            '}';
    }
}
