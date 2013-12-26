package com.ninetwozero.battlechat.json.chat.friendlist;

public enum PresenceType {
    OFFLINE(0),
    ONLINE_WEB(1),
    ONLINE_TABLET(2),
    ONLINE_MOBILE(4),
    ONLINE_GAME(8),
    ONLINE_ORIGIN(16),

    PLAYING_MP(256),
    PLAYING_COOP(512),
    PLAYING_ORIGIN(1024),

    AWAY_WEB(65536),
    AWAY_ORIGIN(131072),

    INVISIBLE_WEB(6777216),
    INVISIBLE_TABLET(33554432),
    INVISIBLE_MOBILE(67108864),

    GROUP_WEB(4294967296L),
    GROUP_ORIGIN(8589934592L);

    private long id;
    PresenceType(final long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public static PresenceType from(final UserPresence presence) {
        if (presence.isPlaying()) {
            return PLAYING_MP;
        } else if (presence.isOnline() && presence.getState() >= AWAY_WEB.getId()) {
            return AWAY_WEB;
        } else if (presence.isOnline()) {
            return ONLINE_WEB;
        } else {
            return OFFLINE;
        }
    }
}
