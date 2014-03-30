package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

public class MultiplayerInformation {
    @SerializedName("personaId")
    private String personaId;
    @SerializedName("levelName")
    private String levelKey;
    @SerializedName("gameMode")
    private int gameMode;
    @SerializedName("serverGuid")
    private String serverGuid;
    @SerializedName("serverName")
    private String serverName;
    @SerializedName("gameId")
    private String gameId;
    @SerializedName("game")
    private int game;
    @SerializedName("platform")
    private int platform;

    public String getPersonaId() {
        return personaId;
    }

    public String getLevelKey() {
        return levelKey;
    }

    public int getGameMode() {
        return gameMode;
    }

    public String getServerGuid() {
        return serverGuid;
    }

    public String getServerName() {
        return serverName;
    }

    public String getGameId() {
        return gameId;
    }

    public int getGame() {
        return game;
    }

    public int getPlatform() {
        return platform;
    }
}
