package com.ninetwozero.battlechat.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.adapters.UserListAdapter;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		startActivity( new Intent(this, ChatActivity.class).putExtra("profileId", id) );
	}

	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new UserListAdapter(getApplicationContext()));
	}
}
