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
