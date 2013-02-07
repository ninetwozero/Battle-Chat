package com.ninetwozero.battlechat.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private final static int OFFLINE = 0;
	private final static int ONLINE = 1;
	private final static int PLAYING = 2;
	
	private long mId;
	private String mName;
	private int mStatus;
	
	public User(Parcel in) {
		mId = in.readLong();
		mName = in.readString();
		mStatus = in.readInt();
	}
	
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
	
	public String getOnlineStatus() {
		switch(mStatus) {
			case 0:
				return "OFFLINE";
			case 1:
				return "ONLINE";
			case 2:
				return "PLAYING";
			default:
				return "UNKNOWN";
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mId);
		out.writeString(mName);
		out.writeInt(mStatus);
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
