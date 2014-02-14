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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseActivity;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.UserLoginEvent;
import com.ninetwozero.battlechat.services.BattleChatService;
import com.ninetwozero.battlechat.ui.about.AboutActivity;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

public class LoginActivity extends BaseActivity {
    private static final String RESET_PASSWORD_LINK = "https://signin.ea.com/p/web/resetPassword";

    private String email;
    private String password;
    private SharedPreferences sharedPreferences;

    private EditText emailView;
    private EditText passwordView;
    private View loginFormView;
    private View loginStatusView;
    private TextView alertText;
    private TextView loginStatusMessageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
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

    private void initialize() {
        setupFromPreExistingData();
        setupLayout();
        displayEula();

    }

    private void displayEula() {
        if (!sharedPreferences.getBoolean(EulaDialogFragment.USER_ACCEPTED_EULA, false)) {
            final FragmentManager manager = getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();
            final Fragment previousDialog = manager.findFragmentByTag(EulaDialogFragment.TAG);
            if (previousDialog != null) {
                transaction.remove(previousDialog);
            }

            final EulaDialogFragment eulaFragment = EulaDialogFragment.newInstance();
            eulaFragment.setCancelable(false);
            eulaFragment.show(transaction, EulaDialogFragment.TAG);
        }
    }

    private void setupFromPreExistingData() {
        setTitle(R.string.title_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (alreadyHasCookie() && BattleChat.isConnectedToNetwork()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        email = sharedPreferences.getString(Keys.Session.EMAIL, "");
        password = sharedPreferences.getString(Keys.Session.PASSWORD, "");
    }

    private boolean alreadyHasCookie() {
        return sharedPreferences.contains(Keys.Session.COOKIE_VALUE) && !sharedPreferences.getString(Keys.Session.COOKIE_VALUE, "").equals("");
    }

    private void setupLayout() {
        setupForm();
        setupMenu();
    }

    private void setupForm() {
        emailView = (EditText) findViewById(R.id.email);
        emailView.setText(email);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setText(password);
        passwordView.setOnEditorActionListener(
            new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_ACTION_SEND) {
                        doLogin();
                        return true;
                    }
                    return false;
                }
            }
        );

        alertText = (TextView) findViewById(R.id.login_alert);
        loginFormView = findViewById(R.id.login_form);
        loginStatusView = findViewById(R.id.login_status);
        loginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        findViewById(R.id.sign_in_button).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BattleChat.isConnectedToNetwork()) {
                        clearErrorMessage();
                        doLogin();
                    }
                }
            }
        );
    }

    private void setupMenu() {
        findViewById(R.id.button_menu).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu menu = new PopupMenu(LoginActivity.this, view);
                    menu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = null;
                                if (item.getItemId() == R.id.menu_about) {
                                    intent = new Intent(LoginActivity.this, AboutActivity.class);
                                } else if (item.getItemId() == R.id.menu_reset_password) {
                                    intent = new Intent(Intent.ACTION_VIEW).setData(
                                        Uri.parse(RESET_PASSWORD_LINK)
                                    );
                                }

                                if (intent != null) {
                                    startActivity(intent);
                                }
                                return true;
                            }
                        }
                    );
                    menu.inflate(R.menu.activity_login);
                    menu.show();
                }
            }
        );
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
            displayErrorMessage(event.getMessage());
        }
    }

    private void displayNetworkNotice(final boolean isConnected) {
        alertText.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        findViewById(R.id.sign_in_button).setEnabled(isConnected);
    }

    private void displayErrorMessage(final String error) {
        alertText.setVisibility(View.VISIBLE);
        alertText.setText(error);
    }

    private void displayErrorMessage(final int errorResource) {
        alertText.setVisibility(View.VISIBLE);
        alertText.setText(errorResource);
    }

    private void clearErrorMessage() {
        alertText.setVisibility(View.GONE);
        alertText.setText("");
    }

    private void showProgress(final boolean show) {
        loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
