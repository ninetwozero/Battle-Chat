package com.ninetwozero.battlechat.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.User;

public class ChatActivity extends AbstractListActivity {

	private Button mButton;
	private EditText mField;
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		setupOtherUser();
		setupForm();
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
		String message = mField.getText().toString();
		if( message.length() == 0 ) {
			showToast("You need to enter a message.");
			return;
		}
		new AsyncPostMessage().execute();
	}
	
	private void toggleButton() {
		mButton.setEnabled(!mButton.isEnabled());
	}
	
	private class AsyncPostMessage extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			showToast("Sending message...");
			toggleButton();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				showToast("Message sent!");
			} else {
				showToast("Message could not be sent!");
			}
			toggleButton();
		}
	}
}
