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

package com.ninetwozero.battlechat;

import org.apache.http.cookie.Cookie;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.misc.Keys;


public class BattleChat extends Application {
	public final static String COOKIE_NAME = "beaker.session.id";
	public final static String COOKIE_DOMAIN = "battlelog.battlefield.com";
	
    private static BattleChat mInstance;
    private static Session mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    public static Context getContext() {
        return mInstance;
    }
    
    public static Session getSession() {
    	return mSession;
    }
    
    public static void setSession(Session session) {
    	mSession = session;
    }
    
    public static void reloadSession(final SharedPreferences sharedPreferences) {
    	mSession = new Session(sharedPreferences);
    }
    
    public static void reloadSession(User user, Cookie cookie, String checksum) {
    	mSession = new Session(user, cookie, checksum);
    }
	
    public static boolean hasSession() {
		return mSession != null;
	}
	
    public static void saveToSharedPreferences(Context c) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putLong(Keys.Session.USER_ID, mSession.getUser().getId());
			editor.putString(Keys.Session.USERNAME, mSession.getUser().getUsername());
			editor.putString(Keys.Session.COOKIE_NAME, mSession.getCookie().getName());
			editor.putString(Keys.Session.COOKIE_VALUE, mSession.getCookie().getValue());
			editor.putString(Keys.Session.CHECKSUM, mSession.getChecksum());

			editor.commit();
    }
    
    public static void clearSession(Context c) {
		BattleChat.mSession = null;
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.remove(Keys.Session.USER_ID);
		editor.remove(Keys.Session.USERNAME);
		editor.remove(Keys.Session.COOKIE_NAME);
		editor.remove(Keys.Session.COOKIE_VALUE);
		editor.remove(Keys.Session.CHECKSUM);
		
		editor.commit();
    }
}
