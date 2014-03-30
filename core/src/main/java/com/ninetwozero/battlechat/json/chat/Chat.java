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

import java.util.List;

public class Chat {
    @SerializedName("chatId")
    private long chatId;
    @SerializedName("unreadCount")
    private int unreadCount;
    @SerializedName("maxSlots")
    private int maxSlots;
    @SerializedName("users")
    private List<User> users;
    @SerializedName("messages")
    private List<Message> messages;
    @SerializedName("isAdminChat")
    private boolean adminChat;

    public long getChatId() {
        return chatId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isAdminChat() {
        return adminChat;
    }
}
