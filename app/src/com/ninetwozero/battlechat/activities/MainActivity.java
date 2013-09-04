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

package com.ninetwozero.battlechat.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractFragmentActivity;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.interfaces.ActivityAccessInterface;
import com.ninetwozero.battlechat.services.BattleChatService;

import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;

public class MainActivity extends AbstractFragmentActivity implements ActivityAccessInterface {
    private SlidingMenu mSlidingMenu;

	public final static String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        setup(savedInstanceState);
    }

	@Override
	public void onResume() {
		super.onResume();
		showNotification();
	}

    @Override
    public void onBackPressed() {
        if( mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
    }

    private void setup(final Bundle icicle) {
        setupSlidingMenu();
        setupActionBar();
    }

    private void setupSlidingMenu() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setSelectorDrawable(R.drawable.slidingmenu_indicator);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        mSlidingMenu.setMenu(R.layout.slidingmenu_main);
    }

    private void setupActionBar() {
        final ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
            case android.R.id.home:
                toggle();
                return true;
			case R.id.menu_about:
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			case R.id.menu_settings:
				startActivity( new Intent(this, SettingsActivity.class));
				return true;
			case R.id.menu_exit:
    			logoutFromWebsite();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void logoutFromWebsite() {
		new LogoutTask().execute();
	}

    @Override
    public void toggleSlidingMenu() {
        mSlidingMenu.toggle(true);
    }

    private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				final Cookie cookie = BattleChat.getSession().getCookie();
				Jsoup.connect(HttpUris.LOGOUT).cookie(cookie.getName(), cookie.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			BattleChat.clearSession(getApplicationContext());
			BattleChat.clearNotification(getApplicationContext());
			BattleChatService.unschedule(getApplicationContext());
			sendToLoginScreen();
		}
	}

    public void toggle() {
        mSlidingMenu.toggle(true);
    }
}
