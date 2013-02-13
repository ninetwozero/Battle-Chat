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

package com.ninetwozero.battlechat.misc;

public class Keys {

	public static class Session {
		public final static String USER_ID = "userId";
		public final static String USERNAME = "username";
		public final static String COOKIE_NAME = "sessionName";
		public final static String COOKIE_VALUE = "sessionValue";
		public final static String CHECKSUM = "sessionChecksum";
		private Session() {}
	}
	
	public static class Settings {
		public final static String NOTIFY_ON_LOGOUT = "notify_dead_session";
		public final static String CHAT_INTERVAL = "chat_refresh_interval";
		public final static String BEEP_ON_NEW = "beep_on_new_message";
		private Settings() {}
	}
	
	private Keys() {}
}
