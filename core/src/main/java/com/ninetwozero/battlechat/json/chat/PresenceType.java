package com.ninetwozero.battlechat.json.chat;

public enum PresenceType {
    PLAYING_MP(100),
    PLAYING_COOP(100),
    PLAYING_ORIGIN(100),

    GROUP_WEB(75),
    GROUP_ORIGIN(75),

    ONLINE_WEB(50),
    ONLINE_TABLET(50),
    ONLINE_MOBILE(50),
    ONLINE_GAME(50),
    ONLINE_ORIGIN(50),

    AWAY_WEB(25),
    AWAY_ORIGIN(25),

    UNKNOWN(0),
    OFFLINE(0);

    private final static int AWAY_BITMASK = 65536;
    private int listPriority;
    PresenceType(final int listPriority) {
        this.listPriority = listPriority;
    }

    public int getListPriority() {
        return this.listPriority;
    }

    public static PresenceType from(final UserPresence presence) {
        if (presence.isPlaying()) {
            return PLAYING_MP;
        } else if (presence.isOnline() && presence.getState() >= AWAY_BITMASK) {
            return AWAY_WEB;
        } else if (presence.isOnline()) {
            return ONLINE_WEB;
        } else {
            return OFFLINE;
        }
    }
}
