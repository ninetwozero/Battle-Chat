package com.ninetwozero.battlechat.abstractions;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.http.BattleChatClient;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

public class AbstractListActivity extends ListActivity {
	
	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		BattleChatClient.setCookie(BattleChat.getSession().getCookie());
	}
	
}
