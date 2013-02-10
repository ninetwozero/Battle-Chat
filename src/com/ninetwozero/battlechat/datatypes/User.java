package com.ninetwozero.battlechat.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	public final static int OFFLINE = 0;
	public final static int ONLINE = 1;
	public final static int PLAYING = 2;
	
	private long mId;
	private String mUsername;
	private int mStatus;
	
	public User(Parcel in) {
		mId = in.readLong();
		mUsername = in.readString();
		mStatus = in.readInt();
	}
	
	public User(long id, String name) {
		mId = id;
		mUsername = name;
		mStatus = ONLINE;
	}
	
	public User(long id, String name, int status) {
		mId = id;
		mUsername = name;
		mStatus = status;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getUsername() {
		return mUsername;
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
		out.writeString(mUsername);
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
