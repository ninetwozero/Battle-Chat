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

package com.ninetwozero.battlechat.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

final public class LoginHtmlParser {
    private final Document mDocument;
    private final String BATTLELOG_AVATAR = "http://battlelog-cdn.battlefield.com/cdnprefix/avatar1/public/base/shared/default-avatar-320.png";

    public LoginHtmlParser(final String html) {
        mDocument = Jsoup.parse(html);
    }

    public LoginHtmlParser(final Document document) {
        mDocument = document.clone();
    }

    public long getUserId() {
        final String id = mDocument.select("#base-header-user-tools .avatar").attr("rel");
        return "".equals(id)? 0 : Long.parseLong(id);
    }

    public String getUsername() {
        return mDocument.select("#base-header-user-tools .header-profile-dropdown .profile > a > div").text();
    }

    public String getChecksum() {
        return mDocument.select("#profile-edit-form input[name=post-check-sum]").val();
    }

    public String getGravatar() {
        final String style = mDocument.select("#base-header-user-tools .avatar").attr("style");
        final Pattern pattern = Pattern.compile(".*/avatar/(\\w+)\\?.*");
        final Matcher matcher = pattern.matcher(style);

        return matcher.matches()? getFullGravatarUrl(matcher.group(1)) : BATTLELOG_AVATAR;
    }

    public boolean hasErrorMessage() {
        Elements error = mDocument.select(".gate-login-errormsg.wfont");
        if (error.isEmpty()) {
            return false;
        } else {
            return error.hasText();
        }
    }

    public String getErrorMessage() {
        return mDocument.select(".gate-login-errormsg.wfont").first().text();
    }

    public boolean isLoggedIn() {
        return mDocument.select(".gate-login-errormsg").isEmpty();
    }

    private String getFullGravatarUrl(final String hash) {
        return "http://www.gravatar.com/avatar/" + hash + ".png?s=320";
    }

    @Override
    public String toString() {
        return "LoginHtmlParser{" +
                ", userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", checksum='" + getChecksum() + '\'' +
                ", gravatar='" + getGravatar() + '\'' +
                ", errorMessage=" + (hasErrorMessage()? getErrorMessage(): "N/A") + '\'' +
                ", loggedIn=" + isLoggedIn() +
                '}';
    }
}
