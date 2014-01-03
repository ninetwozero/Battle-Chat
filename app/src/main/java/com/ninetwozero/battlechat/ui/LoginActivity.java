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

package com.ninetwozero.battlechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.UserLoginEvent;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.services.BattleChatService;
import com.ninetwozero.battlechat.ui.about.AboutActivity;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

public class LoginActivity extends Activity {

    private boolean accept;
    private String email;
    private String password;
    private SharedPreferences sharedPreferences;

    private EditText emailView;
    private EditText passwordView;
    private CheckBox checkbox;
    private View loginFormView;
    private View loginStatusView;
    private View disclaimerView;
    private View disclaimerWrap;
    private View networkText;
    private TextView loginStatusMessageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayNetworkNotice(BattleChat.isConnectedToNetwork());
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void getDataFromBundle(final Bundle in) {
        if (in == null) {
            return;
        }
        accept = in.getBoolean("accept", false);
    }

    private void initialize(final Bundle data) {
        setupFromPreexistingData();
        getDataFromBundle(data);
        setupLayout();

    }

    private void setupFromPreexistingData() {
        setTitle(R.string.title_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (alreadyHasCookie() && BattleChat.isConnectedToNetwork()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        email = sharedPreferences.getString(Keys.Session.EMAIL, "");
    }

    private boolean alreadyHasCookie() {
        return sharedPreferences.contains(Keys.Session.COOKIE_VALUE) && !sharedPreferences.getString(Keys.Session.COOKIE_VALUE, "").equals("");
    }

    private void setupLayout() {
        emailView = (EditText) findViewById(R.id.email);
        emailView.setText(email);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(
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

        networkText = findViewById(R.id.text_network);
        disclaimerView = findViewById(R.id.text_disclaimer);
        disclaimerWrap = findViewById(R.id.wrap_disclaimer);
        loginFormView = findViewById(R.id.login_form);
        loginStatusView = findViewById(R.id.login_status);
        loginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        checkbox = (CheckBox) findViewById(R.id.checkbox_accept);
        findViewById(R.id.sign_in_button).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BattleChat.isConnectedToNetwork()) {
                        doLogin();
                    }
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
        toggleDisclaimer(accept);
    }

    @Override
    public void onSaveInstanceState(final Bundle out) {
        out.putBoolean("accept", checkbox.isChecked());
        super.onSaveInstanceState(out);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doLogin() {
        emailView.setError(null);
        passwordView.setError(null);

        email = emailView.getText().toString();
        password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!email.contains("@")) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            startService(
                BattleChatService.getIntent(getApplicationContext()).putExtra(
                    BattleChatService.INTENT_CALLED_FROM_ACTIVITY, true
                ).putExtra(
                    BattleChatService.INTENT_ACTION, BattleChatService.ACTION_LOGIN
                ).putExtra(
                    BattleChatService.INTENT_INPUT_EMAIL, email
                ).putExtra(
                    BattleChatService.INTENT_INPUT_PASSWORD, password
                )
            );
        }
    }

    @Subscribe
    public void onLoginCompleted(final UserLoginEvent event) {
        showProgress(false);
        if (event.getStatus()) {
            Session.saveToSharedPreferences(getApplicationContext());
            BattleChatService.scheduleRun(getApplicationContext());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleDisclaimer(final boolean showLoginForm) {
        checkbox.setChecked(showLoginForm);
        loginFormView.setVisibility(showLoginForm ? View.VISIBLE : View.GONE);
        disclaimerView.setVisibility(showLoginForm ? View.GONE : View.VISIBLE);
    }

    private void displayNetworkNotice(final boolean isConnected) {
        networkText.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        findViewById(R.id.sign_in_button).setEnabled(isConnected);
    }

    private void showProgress(final boolean show) {
        loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        disclaimerWrap.setVisibility(show ? View.GONE : View.VISIBLE);
        disclaimerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
