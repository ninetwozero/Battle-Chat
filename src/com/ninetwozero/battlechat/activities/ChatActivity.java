package com.ninetwozero.battlechat.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.datatypes.User;

public class ChatActivity extends AbstractListActivity {

	private Button mButton;
	private EditText mField;
	private User mUser;
	
	private ReloadTask mReloadTask;
	private SendMessageTask mSendMessageTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		setupOtherUser();
		setupForm();
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
		mSendMessageTask.execute();
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
	
	private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch( InterruptedException ex ) {
				return Math.random() > 0.5;
			}
			return true;
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
				Thread.sleep(2000);
			} catch( InterruptedException ex ) {
				return Math.random() > 0.5;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				showToast("Message sent!");
			} else {
				showToast("Message could not be sent!");
			}
			mSendMessageTask = null;
			toggleButton();
		}
	}
}
