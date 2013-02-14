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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.adapters.MessageListAdapter;
import com.ninetwozero.battlechat.datatypes.Message;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpHeaders;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.misc.Keys;


public class ChatActivity extends AbstractListActivity {

	public static final String TAG = "ChatActivity";
	public static final String EXTRA_USER = "user";
	
	private User mUser;
	private long mChatId;
	private boolean mFirstRun = true;
	
	private MediaPlayer mMediaPlayer;
	private ReloadTask mReloadTask;
	private SendMessageTask mSendMessageTask;
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		setupOtherUser();
		setupForm();
		setupListView();
		setupFromSavedInstance(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
        startTimer();
        setupMediaPlayer();
	}
	
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        stopMediaPlayer();
    }

	private void stopMediaPlayer() {
		if( mMediaPlayer != null ) {
        	mMediaPlayer.release();
        }
	}

	private void stopTimer() {
		if( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
	}
    
	@Override
	protected void onSaveInstanceState(Bundle out) {
		final MessageListAdapter adapter = (MessageListAdapter) getListView().getAdapter();
		final ArrayList<Message> messages = (ArrayList<Message>) adapter.getItems();
		
		out.putLong("chatId", mChatId);
		out.putParcelable("user", mUser);
		out.putParcelableArrayList("messages", messages);
		out.putBoolean("firstRun", mFirstRun);
		
		super.onSaveInstanceState(out);
	}

	private void setupFromSavedInstance(Bundle in) {
		if( in == null ){
			return;
		}
		final long chatId = in.getLong("chatId");
		final User user = in.getParcelable("user");
		final List<Message> friends = in.getParcelableArrayList("messages");
		final MessageListAdapter adapter = (MessageListAdapter) getListView().getAdapter();
		final boolean firstRun = in.getBoolean("firstRun");
		
		mChatId = chatId;
		mUser = user;
		adapter.setItems(friends);
		mFirstRun = firstRun;
	}

	private void reload() {
		if( mReloadTask == null ) {
			mReloadTask = new ReloadTask();
			mReloadTask.execute();
		}
	}	
	
	private void setupOtherUser() {
		mUser = getIntent().getParcelableExtra("user");
		if( mUser == null ) {
			showToast(R.string.msg_chat_load_fail);
			finish();
		}
	}
	
	private void setupForm() {	
		findViewById(R.id.button_send).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					onSend();
				}	
			}
		);
	}
    
	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new MessageListAdapter(getApplicationContext(), mUser.getUsername()));
	}
	
	private void startTimer() {
		mTimer = new Timer();
        mTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    reload();
                }
            }, 
            0, 
            mSharedPreferences.getInt(Keys.Settings.CHAT_INTERVAL, 25)*1000 //--> ms
        );
	}

	private void setupMediaPlayer() {
		mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
		mMediaPlayer.setVolume(1.0f, 1.0f);
	}
	
	private void onSend() {
		if( mSendMessageTask != null ) {
			showToast(R.string.msg_chat_send_multiple_error);
			return;
		}
		
		EditText field = (EditText) findViewById(R.id.input_message);
		String message = field.getText().toString();
		if( message.length() == 0 ) {
			field.setError(getString(R.string.msg_chat_send_message_error));
			field.requestFocus();
			return;
		}
		
		mSendMessageTask = new SendMessageTask();
		mSendMessageTask.execute(message, String.valueOf(mChatId), BattleChat.getSession().getChecksum());
	}
	
	private void toggleButton(boolean enable) {
		final Button button = (Button) findViewById(R.id.button_send);
		if( enable ) {
			button.setText(R.string.label_send);
			button.setEnabled(true);
		} else {
			button.setText(R.string.label_sending);
			button.setEnabled(false);
		}
		
	}
	
	private void clearInput() {
		((TextView) findViewById(R.id.input_message)).setText("");
	}
	
	private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		private List<Message> mMessages = new ArrayList<Message>();
		private int mUnreadCount = 0;
		
		@Override
		protected void onPreExecute() {
			if( getListView().getCount() == 0 ) {
				toggleLoading(true);
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject result = BattleChatClient.post(
					HttpUris.Chat.MESSAGES.replace("{USER_ID}", String.valueOf(mUser.getId())),
					new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
				);
				
				if( result.has("chatId") ) {
					JSONObject chatObject = result.getJSONObject("chat");
					mMessages = getMessagesFromJSON(chatObject);
					mChatId = result.getLong("chatId");
					mUnreadCount = chatObject.isNull("unreadCount")? 0 : chatObject.getInt("unreadCount");	
					return true;
				}
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				if( mUnreadCount > 0 ) {
					showToast(String.format(getString(R.string.msg_chat_num_unread), mUnreadCount));
					notifyWithSound();
				}
				((MessageListAdapter) getListView().getAdapter()).setItems(mMessages);
				scrollToBottom();
			} else {
				showToast(R.string.msg_chat_reload_fail);
				findViewById(R.id.button_send).setEnabled(false);
				stopTimer();
			}
			toggleLoading(false);
			mReloadTask = null;
		}
		
		private List<Message> getMessagesFromJSON(JSONObject chatObject) throws JSONException {
			List<Message> results = new ArrayList<Message>();
			JSONArray messages = chatObject.getJSONArray("messages");
			JSONObject message = null;
			
			for( int i = 0, max = messages.length(); i < max; i++ ) {
				message = messages.getJSONObject(i); 
				results.add( 
					new Message(
						Long.parseLong(message.getString("chatId")),
						message.getString("message"),
						message.getString("fromUsername"),
						message.getLong("timestamp")
					)
				);
			}
			return results;
		}
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			showToast(R.string.msg_chat_sending_message);
			toggleButton(false);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				JSONObject result = BattleChatClient.post(
					HttpUris.Chat.SEND, 
					HttpHeaders.Post.AJAX,
					new BasicNameValuePair("message", params[0]),
					new BasicNameValuePair("chatId", params[1]),
					new BasicNameValuePair("post-check-sum", params[2])
				);
				return !result.has("error");				
			} catch( Exception ex ) {
				ex.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				showToast(R.string.msg_message_ok);
				clearInput();
				reload();
			} else {
				showToast(R.string.msg_message_fail);
			}
			mSendMessageTask = null;
			toggleButton(true);
		}
	}

	private void notifyWithSound() {
		if( mFirstRun ) {
			mFirstRun = false;
			return;
		}
		
		if( mSharedPreferences.getBoolean(Keys.Settings.BEEP_ON_NEW, true) ) {
			mMediaPlayer.start();
		}
	}

	public void scrollToBottom() {
		getListView().post(
            new Runnable() {
                @Override
                public void run() {
                	getListView().setSelection(getListView().getAdapter().getCount() - 1);
                }
            }
        );
	}
	
	private void toggleLoading(boolean isLoading) {
		if( getListAdapter() == null || getListAdapter().isEmpty() ) {
			final View view = findViewById(R.id.status);
			view.setVisibility(isLoading ? View.VISIBLE : View.GONE);
		}
	}
}
