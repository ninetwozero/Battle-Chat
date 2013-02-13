package com.ninetwozero.battlechat.http;




public class HttpUris {
	
	public final static String MAIN = "http://battlelog.battlefield.com/bf3";
	public final static String MAIN_SECURE = "https://battlelog.battlefield.com/bf3";
	public final static String LOGIN = MAIN_SECURE + "/gate/login";
	
	public class Chat {
		public static final String FRIENDS = MAIN + "/comcenter/sync/";
		public static final String MESSAGES = MAIN + "/comcenter/getChatId/{USER_ID}/";
		public static final String SEND = MAIN + "/comcenter/sendChatMessage/";
		public static final String CLOSE = MAIN + "/comcenter/hideChat/{CHAT_ID}/";
		
		private Chat() {}
	}

	private HttpUris() {}
}
