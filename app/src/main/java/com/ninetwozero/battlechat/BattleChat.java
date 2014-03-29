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

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ninetwozero.battlechat.dao.MessageDAO;
import com.ninetwozero.battlechat.dao.UserDAO;
import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.utils.PresenceTypeSerializer;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;

public class BattleChat extends Application {
    public static final String TAG = "com.ninetwozero.battlechat";
    public static final String COOKIE_NAME = "beaker.session.id";
    public static final String COOKIE_DOMAIN = "battlelog.battlefield.com";
    public static final String BUGSENSE_KEY = "c07b42d4#";
    
    private static BattleChat instance;

    public static Context getContext() {
        return instance;
    }

    public static boolean hasStoredCookie(final SharedPreferences preferences) {
        return !"".equals(preferences.getString(Keys.Session.COOKIE_VALUE, ""));
    }

    public static boolean isConnectedToNetwork() {
        final ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo network = manager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    public static boolean isServiceRunning(final String className) {
        final ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (className.equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());
        sprinkles.registerType(PresenceType.class, new PresenceTypeSerializer());
        sprinkles.addMigration(fetchInitialMigrations());
    }

    private Migration fetchInitialMigrations() {
        final Migration migration = new Migration();
        migration.createTable(MessageDAO.class);
        migration.createTable(UserDAO.class);
        return migration;
    }
}
