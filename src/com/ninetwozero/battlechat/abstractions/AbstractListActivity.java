package com.ninetwozero.battlechat.abstractions;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.http.BattleChatClient;

public class AbstractListActivity extends ListActivity {
	protected SharedPreferences mSharedPreferences;
	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		setupBattleChatClient();
	}

	public void showToast(final String text) {
		if( mToast != null ) { 
			mToast.cancel();
			mToast = null;
		}
		
		mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	private void setupBattleChatClient() {
		if( BattleChat.hasSession() ) {
			BattleChat.reloadSession(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
		}
		BattleChatClient.setCookie(BattleChat.getSession().getCookie());
	}
	
}
