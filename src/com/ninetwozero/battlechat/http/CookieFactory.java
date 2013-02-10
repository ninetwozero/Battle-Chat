package com.ninetwozero.battlechat.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.ninetwozero.battlechat.BattleChat;

public class CookieFactory {

	private CookieFactory() {
	}
	
	public static Cookie build(final String name, final String value) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(BattleChat.COOKIE_DOMAIN);
		return cookie;
	}

}
