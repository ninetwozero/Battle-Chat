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

package com.ninetwozero.battlechat.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.ui.LoginActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    public static void showLoggedInNotification(final Context c) {
        if (c == null) {
            return;
        }

        final String subtitle = String.format(
            c.getString(R.string.text_notification_subtitle_ok),
            Session.getUsername()
        );

        final NotificationManager manager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(R.string.service_name);

        Notification notification = new NotificationCompat.Builder(c)
            .setContentTitle(c.getString(R.string.text_notification_title))
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, LoginActivity.class), 0))
            .build();
        manager.notify(R.string.service_name, notification);
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
}
