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

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.CookieFactory;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.http.LoginHtmlParser;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.services.BattleChatService;

public class LoginActivity extends SherlockActivity {

	public static final String TAG = "LoginActivity";

	private boolean mAccept = false;
	private UserLoginTask mAuthTask = null;
	private String mEmail;
	private String mPassword;
	private SharedPreferences mSharedPreferences;
	
	private EditText mEmailView;
	private EditText mPasswordView;
	private CheckBox mCheckbox;
	private View mLoginFormView;
	private View mLoginStatusView;
	private View mDisclaimerView;
	private View mDisclaimerWrap;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_login);
		getDataFromBundle(savedInstanceState);
		setupLayout();
	}

	private void getDataFromBundle(Bundle in) {
		if( in == null ) {
			return;
		}
		mAccept = in.getBoolean("accept", false);
	}

	private void init() {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		setTitle(R.string.title_login);
		if( alreadyHasCookie() ) {
			startActivity( new Intent(this, MainActivity.class) );
			finish();
		}
	}

	private boolean alreadyHasCookie() {
		return mSharedPreferences.contains(Keys.Session.COOKIE_VALUE) && !mSharedPreferences.getString(Keys.Session.COOKIE_VALUE, "").equals("");
	}

	private void setupLayout() {
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(
			new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
					if (id == R.id.login || id == EditorInfo.IME_NULL) {
						doLogin();
						return true;
					}
					return false;
				}
			}
		);

		mDisclaimerView = findViewById(R.id.text_disclaimer);
		mDisclaimerWrap = findViewById(R.id.wrap_disclaimer);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mCheckbox = (CheckBox) findViewById(R.id.checkbox_accept);
		findViewById(R.id.sign_in_button).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					doLogin();
				}
			}
		);
		
		findViewById(R.id.checkbox_accept).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					final CheckBox checkbox = (CheckBox) view;
					toggleDisclaimer(checkbox.isChecked());
				}
			}
		);
		toggleDisclaimer(mAccept);
	}
	
	@Override
	public void onSaveInstanceState(Bundle out) {
		out.putBoolean("accept", mCheckbox.isChecked());
		super.onSaveInstanceState(out);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == R.id.menu_about ) {
			startActivity( new Intent(this, AboutActivity.class) );
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void doLogin() {
		if (mAuthTask != null) {
			return;
		}

		mEmailView.setError(null);
		mPasswordView.setError(null);

		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mEmail, mPassword);
		}
	}
	
	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show? View.GONE : View.VISIBLE);
		mDisclaimerWrap.setVisibility(show? View.GONE : View.VISIBLE);
		mDisclaimerView.setVisibility(show? View.GONE : View.VISIBLE);
	}
	
	public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
		
		private Session mSession;
		private String mErrorMessage;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Connection connection = Jsoup.connect(HttpUris.LOGIN);
				connection = connection.data(
					"email", params[0], 
					"password", params[1],
					"remember", "1",
					"redirect", "",
					"submit", "Sign+in"
				);
				connection = connection.method(Method.POST);
				Connection.Response result = connection.execute();
				return hasLoggedin(result);
			} catch( Exception ex ) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			
			if (success) {
				BattleChat.setSession(mSession);
				BattleChat.saveToSharedPreferences(getApplicationContext());
				BattleChatService.scheduleRun(getApplicationContext());
				showNotification();
				startActivity( new Intent(LoginActivity.this, MainActivity.class));
				finish();
			} else {
				Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
			}
		}

		private void showNotification() {
			if( mSharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, true) ) {
				BattleChat.showLoggedInNotification(getApplicationContext());
			}
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

		private boolean hasLoggedin(Connection.Response response) throws Exception {
			LoginHtmlParser parser = new LoginHtmlParser(response.parse());
			if( parser.hasErrorMessage() ) {
				mErrorMessage = parser.getErrorMessage();
			} else {
				mSession = new Session(
					new User(parser.getUserId(), parser.getUsername()),
					CookieFactory.build(BattleChat.COOKIE_NAME, response.cookie(BattleChat.COOKIE_NAME)),
					parser.getChecksum()
				);
			}
			return mSession != null;
		}
	}
	
	private void toggleDisclaimer(boolean showLoginForm) {
		mCheckbox.setChecked(showLoginForm);
		mLoginFormView.setVisibility(showLoginForm ? View.VISIBLE : View.GONE);
		mDisclaimerView.setVisibility(showLoginForm ? View.GONE : View.VISIBLE);
	}
}
