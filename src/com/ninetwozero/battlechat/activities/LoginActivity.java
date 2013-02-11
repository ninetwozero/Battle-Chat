package com.ninetwozero.battlechat.activities;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.CookieFactory;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.http.LoginHtmlParser;

public class LoginActivity extends Activity {

	public static final String TAG = "LoginActivity";

	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
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

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					doLogin();
				}
			}
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
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
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
				startActivity( new Intent(LoginActivity.this, MainActivity.class));
				finish();
			} else {
				Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
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
}
