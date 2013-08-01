package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.datatypes.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

// TODO: We have to remove the Parcelable parts of User in able to run this at the moment (as regular JUNIT).
// FIXME: When Junit supports individual bars per assertion, I'll refactor the ten methods into one with a loop

public class UserComparatorTest {
    
	private static List<User> actualUserOrder;
	private static List<User> expectedUserOrder;
	
	public UserComparatorTest() {		
		User user0 = new User(0, "Playing", User.PLAYING_MP);
		User user1 = new User(100, "Jarl Ank", User.PLAYING_COOP);
		User user2 = new User(101, "Jarl Pank", User.PLAYING_ORIGIN);
		User user3 = new User(0, "Online", User.ONLINE_WEB);
		User user5 = new User(201, "Sarl Ank", User.ONLINE_GAME);
		User user4 = new User(200, "Jarl Pank", User.ONLINE_MOBILE);
		User user6 = new User(0, "Offline", User.OFFLINE);
		User user7 = new User(300, "Amk", User.OFFLINE);
		User user8 = new User(301, "Ank", User.OFFLINE);

		actualUserOrder = new ArrayList<User>();
		expectedUserOrder = new ArrayList<User>();
		
		actualUserOrder.add(user2);
		actualUserOrder.add(user7);
		actualUserOrder.add(user0);
		actualUserOrder.add(user1);
		actualUserOrder.add(user5);
		actualUserOrder.add(user8);
		actualUserOrder.add(user3);
		actualUserOrder.add(user6);
		actualUserOrder.add(user4);

		expectedUserOrder.add(user0);
		expectedUserOrder.add(user1);
		expectedUserOrder.add(user2);
		expectedUserOrder.add(user3);
		expectedUserOrder.add(user4);
		expectedUserOrder.add(user5);
		expectedUserOrder.add(user6);
		expectedUserOrder.add(user7);
		expectedUserOrder.add(user8);
		
		Collections.sort(actualUserOrder, new UserComparator());
	}
	
	@Test
	public void testThatAlgorithmWorksOnIndex0() throws Exception {
		assertEquals(expectedUserOrder.get(0), actualUserOrder.get(0));
	}
	
	@Test
	public void testThatAlgorithmWorksOnIndex1() throws Exception {
		assertEquals(expectedUserOrder.get(1), actualUserOrder.get(1));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex2() throws Exception {
		assertEquals(expectedUserOrder.get(2), actualUserOrder.get(2));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex3() throws Exception {
		assertEquals(expectedUserOrder.get(3), actualUserOrder.get(3));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex4() throws Exception {
		assertEquals(expectedUserOrder.get(4), actualUserOrder.get(4));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex5() throws Exception {
		assertEquals(expectedUserOrder.get(5), actualUserOrder.get(5));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex6() throws Exception {
		assertEquals(expectedUserOrder.get(6), actualUserOrder.get(6));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex7() throws Exception {
		assertEquals(expectedUserOrder.get(7), actualUserOrder.get(7));
	}

	@Test
	public void testThatAlgorithmWorksOnIndex8() throws Exception {
		assertEquals(expectedUserOrder.get(8), actualUserOrder.get(8));
	}
	
}
