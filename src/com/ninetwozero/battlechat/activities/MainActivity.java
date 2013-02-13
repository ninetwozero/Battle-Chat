package com.ninetwozero.battlechat.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
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
			mReloadTask = new ReloadTask();
			mReloadTask.execute();
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
		if( user != null ) {
			startActivity( new Intent(this, ChatActivity.class).putExtra(ChatActivity.EXTRA_USER, user) );			
		}
	}

	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new UserListAdapter(getApplicationContext()));
	}
	
	public class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		
		private String mMessage;
		private List<User> mItems;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject result = BattleChatClient.post(
					HttpUris.Chat.FRIENDS, 
					new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
				);
				
				if( result.has("error") ) {
					mMessage = result.getString("error");
					return false;
				} 

				mItems = getUsersFromJson(result);
				return true;
			} catch( Exception ex ) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				((UserListAdapter)getListView().getAdapter()).setItems(mItems);
			} else {
				showToast(mMessage);
			}
			mReloadTask = null;
		}
		
		private List<User> getUsersFromJson(JSONObject result) throws JSONException {
			JSONArray friends = result.getJSONArray("friendscomcenter");
			JSONObject friend;
			JSONObject presence;
			List<User> users = new ArrayList<User>();
			
			int numFriends = friends.length();
			int numPlaying = 0;
			int numOnline = 0;
			int numOffline = 0;
			
			if( numFriends > 0 ) {
				User user = null;
				for( int i = 0; i < numFriends; i++ ) {
					friend = friends.optJSONObject(i);
					presence = friend.getJSONObject("presence");
					
					if( presence.getBoolean("isPlaying") ) {
						user = new User(
							Long.parseLong(friend.getString("userId")),
							friend.getString("username"), 
							User.PLAYING
						);
						numPlaying++;
					} else if( presence.getBoolean("isOnline") ) {
						user = new User(
							Long.parseLong(friend.getString("userId")),
							friend.getString("username"), 
							User.ONLINE
						);
						numOnline++;
					} else {
						user = new User(
							Long.parseLong(friend.getString("userId")),
							friend.getString("username"), 
							User.OFFLINE
						);
						numOffline++;
					}
					users.add(user);
				}

				if (numPlaying > 0) {
					users.add(new User(0, getString(R.string.label_playing), User.PLAYING));
				}

				if (numOnline > 0) {
					users.add(new User(0, getString(R.string.label_online), User.ONLINE));
				}

				if (numOffline > 0) {
					users.add(new User(0, getString(R.string.label_offline), User.OFFLINE));
				}
				Collections.sort(users,	new UserComparator());
			}
			return users;
		}		
	}
}
