package com.ninetwozero.battlechat;
import org.apache.http.cookie.Cookie;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.User;


public class BattleChat extends Application {
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
    
    public static void loadSession(final SharedPreferences sharedPreferences) {
    	mSession = new Session(sharedPreferences);
    }
    
    public static void loadSession(User user, Cookie cookie, String checksum) {
    	mSession = new Session(user, cookie, checksum);
    }
}
