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

package com.ninetwozero.battlechat.datatypes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.ninetwozero.battlechat.misc.Keys;

public class Session {
    private static final Bundle sessionData;

    static {
        sessionData = new Bundle();
    }

    public static void setSession(
        final String userId, final String username, final String email, final String gravatar,
        final String cookieName, final String cookieValue, final String checksum
    ) {
        sessionData.putString(Keys.Session.USER_ID, userId);
        sessionData.putString(Keys.Session.USERNAME, username);
        sessionData.putString(Keys.Session.EMAIL, email);
        sessionData.putString(Keys.Session.GRAVATAR, gravatar);
        sessionData.putString(Keys.Session.COOKIE_NAME, cookieName);
        sessionData.putString(Keys.Session.COOKIE_VALUE, cookieValue);
        sessionData.putString(Keys.Session.CHECKSUM, checksum);
    }

    public static void loadSession(final SharedPreferences sharedPreferences) {
        sessionData.putString(Keys.Session.USER_ID, sharedPreferences.getString(Keys.Session.USER_ID, ""));
        sessionData.putString(Keys.Session.USERNAME, sharedPreferences.getString(Keys.Session.USERNAME, ""));
        sessionData.putString(Keys.Session.EMAIL, sharedPreferences.getString(Keys.Session.EMAIL, ""));
        sessionData.putString(Keys.Session.GRAVATAR, sharedPreferences.getString(Keys.Session.GRAVATAR, ""));
        sessionData.putString(Keys.Session.COOKIE_NAME, sharedPreferences.getString(Keys.Session.COOKIE_NAME, ""));
        sessionData.putString(Keys.Session.COOKIE_VALUE, sharedPreferences.getString(Keys.Session.COOKIE_VALUE, ""));
        sessionData.putString(Keys.Session.CHECKSUM, sharedPreferences.getString(Keys.Session.CHECKSUM, ""));
    }

    public static boolean hasSession() {
        return (
            Session.sessionData != null && Session.sessionData.size() > 0 &&
            !"".equals(Session.sessionData.getString(Keys.Session.COOKIE_VALUE))
        );
    }
    public static Bundle getSession() {
        return Session.sessionData;
    }

    /* Convenience methods */
    public static String getUserId() {
        return Session.sessionData.getString(Keys.Session.USER_ID, "0");
    }

    public static String getUsername() {
        return Session.sessionData.getString(Keys.Session.USERNAME, "N/A");
    }

    public static String getEmail() {
        return Session.sessionData.getString(Keys.Session.EMAIL, "john.doe@example.com");
    }

    public static String getGravatarUrl() {
        return Session.sessionData.getString(Keys.Session.GRAVATAR, "");
    }

    public static String getCookieName() {
        return Session.sessionData.getString(Keys.Session.COOKIE_NAME, "beaker.sessionData.id");
    }

    public static String getCookieValue() {
        return Session.sessionData.getString(Keys.Session.COOKIE_VALUE, "");
    }

    public static String getChecksum() {
        return Session.sessionData.getString(Keys.Session.CHECKSUM, "");
    }

    /* More fun functionality */
    public static void saveToSharedPreferences(final Context c) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        final SharedPreferences.Editor editor = preferences.edit();
        final Bundle sessionData = Session.getSession();

        editor.putString(Keys.Session.USER_ID, sessionData.getString(Keys.Session.USER_ID));
        editor.putString(Keys.Session.USERNAME, sessionData.getString(Keys.Session.USERNAME));
        editor.putString(Keys.Session.EMAIL, sessionData.getString(Keys.Session.EMAIL));
        editor.putString(Keys.Session.GRAVATAR, sessionData.getString(Keys.Session.GRAVATAR));
        editor.putString(Keys.Session.COOKIE_NAME, sessionData.getString(Keys.Session.COOKIE_NAME));
        editor.putString(Keys.Session.COOKIE_VALUE, sessionData.getString(Keys.Session.COOKIE_VALUE));
        editor.putString(Keys.Session.CHECKSUM, sessionData.getString(Keys.Session.CHECKSUM));

        editor.commit();
    }

    public static void clearSession(final Context c) {
        Session.sessionData.clear();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(Keys.Session.USER_ID);
        editor.remove(Keys.Session.USERNAME);
        // editor.remove(Keys.Session.EMAIL);
        editor.remove(Keys.Session.GRAVATAR);
        editor.remove(Keys.Session.COOKIE_NAME);
        editor.remove(Keys.Session.COOKIE_VALUE);
        editor.remove(Keys.Session.CHECKSUM);

        editor.commit();
    }


}
