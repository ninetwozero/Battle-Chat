package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserComparatorTest {

    private static List<User> usersAutomatic;
    private static List<User> usersManual;

    public UserComparatorTest() {
        User user1 = new User("100", "Jarl Ank", "", PresenceType.PLAYING_MP);
        User user2 = new User("101", "Jarl Pank", "", PresenceType.PLAYING_MP);
        User user3 = new User("200", "Darl Ank", "", PresenceType.ONLINE_WEB);
        User user4 = new User("202", "Sarl Pank", "", PresenceType.ONLINE_WEB);
        User user5 = new User("201", "Jarl Stank", "", PresenceType.AWAY_WEB);
        User user6 = new User("203", "Zarl Zank", "", PresenceType.AWAY_WEB);
        User user7 = new User("300", "Amk", "", PresenceType.OFFLINE);
        User user8 = new User("301", "Ank", "", PresenceType.OFFLINE);

        usersAutomatic = new ArrayList<User>();
        usersManual = new ArrayList<User>();

        usersAutomatic.add(user2);
        usersAutomatic.add(user6);
        usersAutomatic.add(user8);
        usersAutomatic.add(user1);
        usersAutomatic.add(user4);
        usersAutomatic.add(user7);
        usersAutomatic.add(user3);
        usersAutomatic.add(user5);

        usersManual.add(user1);
        usersManual.add(user2);
        usersManual.add(user3);
        usersManual.add(user4);
        usersManual.add(user5);
        usersManual.add(user6);
        usersManual.add(user7);
        usersManual.add(user8);

        Collections.sort(usersAutomatic, new UserComparator());
    }

    @Test
    public void autoSortShouldMatchOurManualSorting() {
        for (int i = 0, max = usersAutomatic.size(); i < max; i++) {
            Assert.assertEquals(usersManual.get(i), usersAutomatic.get(i));
        }
    }
}