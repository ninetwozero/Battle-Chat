package com.ninetwozero.battlechat.datatypes;

import org.apache.http.cookie.Cookie;

import android.content.SharedPreferences;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.http.CookieFactory;
import com.ninetwozero.battlechat.misc.Keys;

public class Session {
	
	private User mUser;
	private Cookie mCookie;
	private String mChecksum;
	
	public Session(SharedPreferences sharedPreferences) {
		mUser = new User(
			sharedPreferences.getLong(Keys.Session.USER_ID, 0),
			sharedPreferences.getString(Keys.Session.USERNAME, "N/A"),
			User.ONLINE
		);
		mCookie = CookieFactory.build(
			sharedPreferences.getString(Keys.Session.COOKIE_NAME, BattleChat.COOKIE_NAME),
			sharedPreferences.getString(Keys.Session.COOKIE_VALUE, "")
		);
		mChecksum = sharedPreferences.getString(Keys.Session.CHECKSUM, "");
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

	@Override
	public String toString() {
		return "Session [mUser=" + mUser + ", mCookie=" + mCookie
				+ ", mChecksum=" + mChecksum + "]";
	}
}
