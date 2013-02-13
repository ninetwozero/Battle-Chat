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

/* TODO: notify new messages */

public class ChatActivity extends AbstractListActivity {

	public static final String TAG = "ChatActivity";
	public static final String EXTRA_USER = "user";
	
	private Button mButton;
	private EditText mField;
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
	}
	
	@Override
	public void onResume() {
		super.onResume();
        setupTimer();
        setupMediaPlayer();
	}

	private void reload() {
		if( mReloadTask == null ) {
			mReloadTask = new ReloadTask();
			mReloadTask.execute();
		}
	}

    @Override
    public void onPause() {
        super.onPause();
        if( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
        
        if( mMediaPlayer != null ) {
        	mMediaPlayer.release();
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
		mButton = (Button) findViewById(R.id.button_send);
		mField = (EditText) findViewById(R.id.input_message);
	
		mButton.setOnClickListener(
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
	
	private void setupTimer() {
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
		
		String message = mField.getText().toString();
		if( message.length() == 0 ) {
			mField.setError(getString(R.string.msg_chat_send_message_error));
			mField.requestFocus();
			return;
		}
		
		mSendMessageTask = new SendMessageTask();
		mSendMessageTask.execute(message, String.valueOf(mChatId), BattleChat.getSession().getChecksum());
	}
	
	private void toggleButton() {
		if( mButton.isEnabled() ) {
			mButton.setText(R.string.label_sending);
			mButton.setEnabled(false);
		} else {
			mButton.setText(R.string.label_send);
			mButton.setEnabled(true);
		}
		
	}
	
	private void clearInput() {
		((TextView) findViewById(R.id.input_message)).setText("");
	}
	
	private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		private List<Message> mMessages = new ArrayList<Message>();
		private int mUnreadCount = 0;
		
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
			}
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
			toggleButton();
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
			toggleButton();
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
}
