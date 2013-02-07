package com.ninetwozero.battlechat.activities;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.R.layout;
import com.ninetwozero.battlechat.R.menu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

public class ChatActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
