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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.ui.LoginActivity;

public class BattleChat extends Application {
    public final static String TAG = "com.ninetwozero.battlechat";
    public final static String COOKIE_NAME = "beaker.session.id";
    public final static String COOKIE_DOMAIN = "battlelog.battlefield.com";
    private static BattleChat instance;

    public static Context getContext() {
        return instance;
    }

    public static void showLoggedInNotification(final Context c) {
        if (c == null) {
            return;
        }

        final String subtitle = String.format(
            c.getString(R.string.text_notification_subtitle_ok),
            Session.getUsername()
        );

        final NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.service_name);

        Notification notification = new NotificationCompat.Builder(c)
            .setContentTitle(c.getString(R.string.text_notification_title))
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, LoginActivity.class), 0))
            .build();
        notificationManager.notify(R.string.service_name, notification);
    }

    public static void showLoggedOutNotification(final Context c) {
        final NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.service_name);

        Notification notification = new NotificationCompat.Builder(c)
            .setContentTitle(c.getString(R.string.text_notification_title))
            .setContentText(c.getString(R.string.text_notification_subtitle_fail))
            .setSmallIcon(R.drawable.ic_launcher)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, LoginActivity.class), 0))
            .build();
        notificationManager.notify(R.string.service_name, notification);
    }

    public static void clearNotification(final Context c) {
        final NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.service_name);
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
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
