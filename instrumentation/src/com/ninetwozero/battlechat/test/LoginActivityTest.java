package com.ninetwozero.battlechat.test;

import android.test.ActivityInstrumentationTestCase2;

import com.ninetwozero.battlechat.activities.LoginActivity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    public void testActivity() {
        LoginActivity activity = getActivity();
        assertNotNull(activity);
    }
}
