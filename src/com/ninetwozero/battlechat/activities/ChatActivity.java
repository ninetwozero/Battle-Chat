package com.ninetwozero.battlechat.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class ChatActivity extends AbstractListActivity {

	public static final String TAG = "ChatActivity";
	private Button mButton;
	private EditText mField;
	private User mUser;
	private long mChatId;
	
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
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
	
	private void setupOtherUser() {
		mUser = getIntent().getParcelableExtra("user");
		if( mUser == null ) {
			showToast("Invalid user selected for this chat.");
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
            25 // TODO: SharedPreferences
        );
	}
	
	private void onSend() {
		if( mSendMessageTask != null ) {
			showToast("You're already trying to login!");
			return;
		}
		
		String message = mField.getText().toString();
		if( message.length() == 0 ) {
			mField.setError("You need to enter a message.");
			mField.requestFocus();
			return;
		}
		
		mSendMessageTask = new SendMessageTask();
		mSendMessageTask.execute(message, String.valueOf(mChatId), BattleChat.getSession().getChecksum());
	}
	
	private void toggleButton() {
		if( mButton.isEnabled() ) {
			mButton.setText("Sending...");
			mButton.setEnabled(false);
		} else {
			mButton.setText("Send");
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
					showToast("Number of new messages: " + mUnreadCount);
				}
				((MessageListAdapter) getListView().getAdapter()).setItems(mMessages);
			} else {
				showToast("Could not reload chat. Please try to relogin.");
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
			showToast("Sending message...");
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
				showToast("Message sent!");
				clearInput();
				reload();
			} else {
				showToast("Message could not be sent!");
			}
			mSendMessageTask = null;
			toggleButton();
		}
	}
}
