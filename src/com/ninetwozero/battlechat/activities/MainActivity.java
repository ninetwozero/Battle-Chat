package com.ninetwozero.battlechat.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.datatypes.User;

public class MainActivity extends AbstractListActivity {

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
			new ReloadTask().execute();
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
		startActivity( new Intent(this, ChatActivity.class).putExtra("user", user) );
	}

	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new UserListAdapter(getApplicationContext()));
	}
	
	public class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		
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
}
