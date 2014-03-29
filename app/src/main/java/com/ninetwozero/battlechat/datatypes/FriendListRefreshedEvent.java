package com.ninetwozero.battlechat.datatypes;

public class FriendListRefreshedEvent {
    private final boolean successful;

    public FriendListRefreshedEvent(boolean status) {
        this.successful = status;
    }

    public boolean wasSuccessful() {
        return successful;
    }
}
