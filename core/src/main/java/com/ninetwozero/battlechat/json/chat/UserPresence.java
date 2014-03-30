package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

public class UserPresence {
    @SerializedName("isOnline")
    private boolean online;
    @SerializedName("isPlaying")
    private boolean playing;
    @SerializedName("onlineGame")
    private SoldierInformation soldierInformation;
    @SerializedName("playingMp")
    private MultiplayerInformation  multiplayerInformation;
    @SerializedName("presenceStates")
    private long state;

    public boolean isOnline() {
        return online;
    }

    public boolean isPlaying() {
        return playing;
    }

    public SoldierInformation getSoldierInformation() {
        return soldierInformation;
    }

    public MultiplayerInformation getMultiplayerInformation() {
        return multiplayerInformation;
    }

    public long getState() {
        return state;
    }
}
