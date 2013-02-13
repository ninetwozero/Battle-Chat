/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

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
