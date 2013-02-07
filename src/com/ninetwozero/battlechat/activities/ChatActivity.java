package com.ninetwozero.battlechat.activities;

import android.app.ListActivity;
import android.os.Bundle;

import com.ninetwozero.battlechat.R;

public class ChatActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
	}
}
