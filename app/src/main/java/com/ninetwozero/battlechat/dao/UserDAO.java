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

package com.ninetwozero.battlechat.dao;

import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.PrimaryKey;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Friends")
public class UserDAO extends Model {
    public static final String TABLE_NAME = "Friends";

    @PrimaryKey
    @Column("id")
    private String id;

    @Column("username")
    private String username;

    @Column("gravatarhash")
    private String gravatarHash;

    @Column("presence")
    private PresenceType presenceType;

    @Column("localUserId")
    private String localUserId;

    public UserDAO() {}

    public UserDAO(User user, String localUserId) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.gravatarHash = user.getGravatarHash();
        this.presenceType = user.getPresenceType();
        this.localUserId = localUserId;
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

    public PresenceType getPresenceType() {
        return presenceType;
    }

    public String getLocalUserId() {
        return localUserId;
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
}
