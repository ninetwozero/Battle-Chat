package com.ninetwozero.battlechat.comparators;

import java.util.Comparator;

import com.ninetwozero.battlechat.datatypes.User;

public class UserComparator implements Comparator<User> {
    public int compare(User u1, User u2) {
    	if( u1.isPlaying() && !u2.isPlaying() || u1.isOnline() && u2.isOffline() ) {
    		return -1;
    	} else if( u1.isOffline() && u2.isOnline() || !u1.isPlaying() && u2.isPlaying() ) {
    		return 1;
    	} else if( u1.getId() == u2.getId() ) {
    		return 0;
    	} else {
    		if( u1.getId() == 0 ) {
    			return -1;
    		} else if( u2.getId() == 0 ) {
    			return 1; 
    		} else {
        		return u1.getUsername().compareToIgnoreCase(u2.getUsername());
    		}
    	}
    }
}
