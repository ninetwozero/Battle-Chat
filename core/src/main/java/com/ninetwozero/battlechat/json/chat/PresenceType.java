package com.ninetwozero.battlechat.json.chat;

public enum PresenceType {
    PLAYING_MP(1001, 100),
    PLAYING_COOP(1002, 100),
    PLAYING_ORIGIN(1003, 100),

    GROUP_WEB(751, 75),
    GROUP_ORIGIN(752, 75),

    ONLINE_WEB(501, 50),
    ONLINE_TABLET(502, 50),
    ONLINE_MOBILE(503, 50),
    ONLINE_GAME(504, 50),
    ONLINE_ORIGIN(505, 50),

    AWAY_WEB(251, 25),
    AWAY_ORIGIN(252, 25),

    UNKNOWN(1, 0),
    OFFLINE(2, 0);

    private final static int AWAY_BITMASK = 65536;
    private final int id;
    private final int listPriority;
    PresenceType(final int id, final int listPriority) {
        this.id = id;
        this.listPriority = listPriority;
    }

    public int getId() {
        return this.id;
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

    public static PresenceType from(final int id) {
        for (PresenceType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No presence for ID=" + id);
    }
}
