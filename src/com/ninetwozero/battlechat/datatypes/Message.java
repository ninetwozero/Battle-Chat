package com.ninetwozero.battlechat.datatypes;

public class Message {

	private long mId;
	private String mContent;
	private User mUser;
	private long mTimestamp;
	
	public Message(long id, String content, long timestamp) {
		mId = id;
		mContent = content;
		mTimestamp = timestamp;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getContent() {
		return mContent;
	}
	
	public User getUser() {
		return mUser;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
}
