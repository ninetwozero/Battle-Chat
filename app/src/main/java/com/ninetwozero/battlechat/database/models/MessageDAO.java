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

package com.ninetwozero.battlechat.database.models;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.battlechat.json.chat.Message;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrementPrimaryKey;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Messages")
public class MessageDAO extends Model {
    public static final String TABLE_NAME = "Messages";

    @AutoIncrementPrimaryKey
    @Column("id")
    private long id;
    @Column("author")
    private String username;
    @Column("content")
    private String content;
    @Column("timestamp")
    private long timestamp;
    @Column("chatId")
    private long chatId;

    public MessageDAO(final Message message) {
        this.username = message.getUsername();
        this.content = message.getMessage();
        this.timestamp = message.getTimestamp();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getChatId() { return chatId; }
}
