/*
 *
 * 	This file is part of BattleChat
 *
 * 	BattleChat is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	BattleChat is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * /
 */

package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

public class GroupJoinedGame {
    @SerializedName("username")
    private String username;
    @SerializedName("serverGuid")
    private String serverGuid;
    @SerializedName("serverName")
    private String serverName;

    public String getUsername() {
        return username;
    }

    public String getServerGuid() {
        return serverGuid;
    }

    public String getServerName() {
        return serverName;
    }
}
