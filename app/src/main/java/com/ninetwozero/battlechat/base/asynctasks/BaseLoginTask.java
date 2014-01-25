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

package com.ninetwozero.battlechat.base.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.LoginRequestFactory;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.network.LoginHtmlParser;
import com.ninetwozero.battlechat.utils.NotificationHelper;

import org.jsoup.Connection;

public abstract class BaseLoginTask extends AsyncTask<String, Void, Boolean> {
    protected String message;
    protected final SharedPreferences sharedPreferences;
    private final Context context;
    private String suppliedEmail;
    private String suppliedPassword;

    public BaseLoginTask(final Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected Boolean doInBackground(final String... params) {
        if (params.length != 2) {
            message = "Invalid length of input data.";
            return false;
        }

        suppliedEmail = params[0];
        suppliedPassword = params[1];

        try {
            final Connection connection = LoginRequestFactory.create(suppliedEmail, suppliedPassword);
            final Connection.Response result = connection.execute();
            return hasLoggedin(result);
        } catch (Exception ex) {
            message = ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected abstract void onPostExecute(final Boolean success);

    protected void showNotification() {
        if (sharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, true)) {
            NotificationHelper.showLoggedInNotification(context);
        }
    }

    private boolean hasLoggedin(final Connection.Response response) throws Exception {
        final LoginHtmlParser parser = new LoginHtmlParser(response.parse());
        if (parser.hasErrorMessage()) {
            message = parser.getErrorMessage();
        } else {
            Session.setSession(
                parser.getUserId(),
                parser.getUsername(),
                suppliedEmail,
                parser.getGravatarUrl(),
                BattleChat.COOKIE_NAME,
                response.cookie(BattleChat.COOKIE_NAME),
                parser.getChecksum()
            );

            // Save the password separately - we don't wanna pass it around in the memory later on
            sharedPreferences.edit().putString(Keys.Session.PASSWORD, suppliedPassword).apply();
        }
        return Session.hasSession();
    }
}
