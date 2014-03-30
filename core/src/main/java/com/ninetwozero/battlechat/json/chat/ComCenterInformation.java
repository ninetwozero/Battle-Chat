package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComCenterInformation {
    @SerializedName("chats")
    private List<Chat> chats;
    @SerializedName("friendscomcenter")
    private List<User> friends;

    public List<Chat> getChats() {
        return chats;
    }

    public List<User> getFriends() {
        return friends;
    }
}
