package com.ninetwozero.battlechat.datatypes;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.SharedPreferences;

public class Session {
	
	private User mUser;
	private Cookie mCookie;
	private String mChecksum;
	
	public Session(SharedPreferences sharedPreferences) {
		mUser = new User(
			sharedPreferences.getLong("userId", 0),
			sharedPreferences.getString("username", "N/A"),
			User.ONLINE
		);
		mCookie = new BasicClientCookie(
			sharedPreferences.getString("sessionName", "beaker.session.id"),
			sharedPreferences.getString("sessionValue", "")
		);
		mChecksum = sharedPreferences.getString("sessionChecksum", "");
	}
	
	public Session(User user, Cookie cookie, String checksum) {
		mUser = user;
		mCookie = cookie;
		mChecksum = checksum;
	}
	
	public User getUser() {
		return mUser;
	}
	
	public Cookie getCookie() {
		return mCookie;
	}
	
	public String getChecksum() {
		return mChecksum;
	}
}
