package com.ninetwozero.battlechat.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ninetwozero.battlechat.R;

public class ChatActivity extends AbstractListActivity {

	private Button mButton;
	private EditText mField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		setupForm();
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
		
		// Do send here
	}
}
