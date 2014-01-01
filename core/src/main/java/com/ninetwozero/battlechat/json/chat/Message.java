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

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("fromUsername")
    private String username;
    @SerializedName("message")
    private String content;
    @SerializedName("timestamp")
    private long timestamp;

    // Used for stupid group chats
    private Type type;
    private Object extra;

    public String getMessage() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Type getType() { return type; }

    public boolean hasExtra() {
        return extra != null;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(final Object object) {
        this.extra = object;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public enum Type {
        MESSAGE,
        GROUP_MESSAGE,
        INVITED_TO_GROUP,
        JOINED_GROUP,
        JOINED_GAME,
        LEFT_GROUP
    }
}
