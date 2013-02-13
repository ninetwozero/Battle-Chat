package com.ninetwozero.battlechat.datatypes;



public class Message {

	private long mId;
	private String mContent;
	private String mUsername;
	private long mTimestamp;
	
	public Message(long id, String content, String user, long timestamp) {
		mId = id;
		mContent = content;
		mUsername = user;
		mTimestamp = timestamp;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getMessage() {
		return mContent;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
}
