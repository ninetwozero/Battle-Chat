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
import android.os.AsyncTask;

import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.services.BattleChatService;
import com.ninetwozero.battlechat.utils.NotificationHelper;

public class BaseLogoutTask extends AsyncTask<Void, Void, Void> {
    private final Context context;

    public BaseLogoutTask(final Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new SimpleGetRequest(UrlFactory.buildLogoutUrl()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void results) {
        Session.clearSession(context);
        NotificationHelper.clearNotification(context);
        BattleChatService.unscheduleRun(context);
    }
}