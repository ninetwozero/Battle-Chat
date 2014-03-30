package com.ninetwozero.battlechat.datatypes;

import com.ninetwozero.battlechat.json.chat.User;

public class ChatRefreshedEvent {
    private long chatId;
    private User user;
    private int newMessageCount;
    private int unreadCount;

    public ChatRefreshedEvent(final long chatId, final User user, final int newMessageCount, final int unreadCount) {
        this.chatId = chatId;
        this.user = user;
        this.newMessageCount = newMessageCount;
        this.unreadCount = unreadCount;
    }

    public long getChatId() {
        return chatId;
    }

    public User getUser() {
        return user;
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
