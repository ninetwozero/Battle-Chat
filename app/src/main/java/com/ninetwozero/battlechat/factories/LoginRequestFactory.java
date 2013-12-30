/*
 *
 * 	This file is part of BattleChat
 *
 * 	BattleChat is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	BattleChat is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * /
 */

package com.ninetwozero.battlechat.factories;

import com.ninetwozero.battlechat.network.LoginHtmlParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class LoginRequestFactory {
    public static final int LOGIN_TIMEOUT = 30000;
    public static final String REDIRECT_PATH = "|bf4|";
    public static final String REMEMBER_VALUE = "1";
    public static final String SUBMIT_VALUE = "Log in";

    public static Connection create(final String email, final String password) {
        return Jsoup.connect(UrlFactory.buildLoginUrl())
            .data(buildKeyValueArray(email, password))
            .followRedirects(true)
            .timeout(LOGIN_TIMEOUT)
            .method(Connection.Method.POST)
            .userAgent(LoginHtmlParser.USER_AGENT_CHROME);
    }

    private static String[] buildKeyValueArray(final String email, final String password) {
        return new String[]{
            "email", email,
            "password", password,
            "remember", REMEMBER_VALUE,
            "redirect", REDIRECT_PATH,
            "submit", SUBMIT_VALUE
        };
    }
}
