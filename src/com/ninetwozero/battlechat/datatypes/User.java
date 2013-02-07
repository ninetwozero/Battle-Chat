package com.ninetwozero.battlechat.datatypes;

public class User {
	private final static int OFFLINE = 0;
	private final static int ONLINE = 1;
	private final static int PLAYING = 2;
	
	private long mId;
	private String mName;
	private int mStatus;
	
	public User(long id, String name, int status) {
		mId = id;
		mName = name;
		mStatus = status;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public boolean isOnline() {
		return mStatus == ONLINE;
	}
	
	public boolean isPlaying() {
		return mStatus == PLAYING;
	}
}
