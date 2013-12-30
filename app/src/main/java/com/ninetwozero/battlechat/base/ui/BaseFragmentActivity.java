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

package com.ninetwozero.battlechat.base.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.ui.LoginActivity;

public class BaseFragmentActivity extends FragmentActivity {
    protected SharedPreferences sharedPreferences;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (BattleChat.hasStoredCookie(sharedPreferences)) {
            setupBattleChatClient();
            showNotification();
        } else {
            sendToLoginScreen();
        }
    }

    public void showToast(final int resource) {
        showToast(getString(resource));
    }

    public void showToast(final String text) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setupBattleChatClient() {
        if (!Session.hasSession() && BattleChat.hasStoredCookie(sharedPreferences)) {
            Session.loadSession(sharedPreferences);
        }
    }

    protected void showNotification() {
        if (sharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, false)) {
            BattleChat.showLoggedInNotification(getApplicationContext());
        } else {
            BattleChat.clearNotification(getApplicationContext());
        }
    }

    final protected void sendToLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
