package com.ninetwozero.battlechat.activities;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpHeaders;
import com.ninetwozero.battlechat.http.HttpUris;

public class MainActivity extends AbstractListActivity {

	public final static String TAG = "MainActivity";
	
	private ReloadTask mReloadTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupListView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	private void reload() {
		if( mReloadTask == null ) {
			new ReloadTask().execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		User user = (User) view.getTag();
		startActivity( new Intent(this, ChatActivity.class).putExtra("user", user) );
	}

	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new UserListAdapter(getApplicationContext()));
	}
	
	public class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		
		private String message;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject result = BattleChatClient.post(
					HttpUris.Chat.FRIENDS, 
					HttpHeaders.Post.AJAX, 
					new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
				);
				
				if( result.has("error") ) {
					message = result.getString("error");
					return false;
				} 
				/* TODO: HANDLE OK */
				return true;
			} catch( Exception ex ) {
				ex.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				/* TODO: UPDATE ADAPTER */
			} else {
				showToast(message);
			}
		}
		
	}
}
