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

package com.ninetwozero.battlechat.json.chat;

public class User {
    private final String id;
    private final String username;
    private final String gravatarHash;
    private UserPresence presence;
    private final PresenceType presenceType;

    public User(final String id, final String username, final String gravatar) {
        this.id = id;
        this.username = username;
        this.gravatarHash = gravatar;
        this.presenceType = PresenceType.UNKNOWN;
    }

    public User(final String id, final String username, final String gravatar, final PresenceType type) {
        this.id = id;
        this.username = username;
        this.gravatarHash = gravatar;
        this.presenceType = type;
    }

    public User(
        final String id, final String username, final String gravatar,
        final UserPresence presence, final PresenceType presenceType
    ) {
        this.id = id;
        this.username = username;
        this.gravatarHash = gravatar;
        this.presence = presence;
        this.presenceType = presenceType;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getGravatarHash() {
        return gravatarHash;
    }

    public UserPresence getPresence() {
        return presence;
    }

    public PresenceType getPresenceType() {
        return presenceType;
    }

    public boolean isOffline() {
        return presenceType == PresenceType.OFFLINE;
    }

    public boolean isAway() {
        return (presenceType == PresenceType.AWAY_ORIGIN || presenceType == PresenceType.AWAY_WEB);
    }

    public boolean isOnline() {
        return presenceType != PresenceType.OFFLINE;
    }

    public boolean isPlaying() {
        return (
            presenceType == PresenceType.PLAYING_COOP ||
                presenceType == PresenceType.PLAYING_MP ||
                presenceType == PresenceType.PLAYING_ORIGIN
        );
    }

    public boolean isGroup() {
        return (
            presenceType == PresenceType.GROUP_ORIGIN ||
            presenceType == PresenceType.GROUP_WEB
        );
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", gravatarHash='" + gravatarHash + '\'' +
            ", presence=" + presence +
            ", presenceType=" + presenceType +
            '}';
    }
}
