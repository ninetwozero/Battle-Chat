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
        public final static String USER_ID = "sessionUserId";
        public final static String USERNAME = "sessionUsername";
        public final static String EMAIL = "sessionEmail";
        public static final String PASSWORD = "sessionPassword";
        public final static String GRAVATAR = "sessionGravatar";
        public final static String COOKIE_NAME = "sessionCookieName";
        public final static String COOKIE_VALUE = "sessionCookieValue";
        public final static String CHECKSUM = "sessionChecksum";

        private Session() {
        }
    }

    public static class Profile {
        public static final String ID = "profileId";
        public static final String USERNAME = "username";
        public static final String NAME = "name";
        public static final String GRAVATAR_HASH = "gravatarHash";
        public static final String PRESENCE = "presence";

        private Profile() {

        }
    }

    public static class Settings {
        public final static String PERSISTENT_NOTIFICATION = "persistent_notification";
        public final static String CHAT_INTERVAL = "chat_refresh_interval";
        public final static String BEEP_ON_NEW = "beep_on_new_message";

        private Settings() {
        }
    }

    public static class Chat {
        public final static String CHAT_ID = "chatId";
        public final static String MESSAGE = "message";
        public final static String CHECKSUM = "post-check-sum";
    }

    private Keys() {
    }
}
