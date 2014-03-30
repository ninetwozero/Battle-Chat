package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

public class SoldierInformation {
    @SerializedName("personaId")
    private String personaId;
    @SerializedName("game")
    private int gameId;
    @SerializedName("platform")
    private int platform;
}
