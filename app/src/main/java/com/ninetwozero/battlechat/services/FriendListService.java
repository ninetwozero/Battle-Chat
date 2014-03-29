package com.ninetwozero.battlechat.services;

import android.app.Service;
import android.content.Intent;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.base.BaseApiService;
import com.ninetwozero.battlechat.dao.UserDAO;
import com.ninetwozero.battlechat.datatypes.FriendListRefreshedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.ComCenterInformation;
import com.ninetwozero.battlechat.json.chat.ComCenterRequest;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.utils.BusProvider;

import java.util.List;

import se.emilsjolander.sprinkles.Transaction;

public class FriendListService extends BaseApiService {

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        BattleChat.getRequestQueue().add(
            new SimpleGetRequest<Boolean>(
                UrlFactory.buildFriendListURL(),
                this
            ) {
                @Override
                protected Boolean doParse(String json) {
                    final Transaction transaction = new Transaction();
                    final ComCenterRequest request = fromJson(json, ComCenterRequest.class);
                    final ComCenterInformation information = request.getInformation();
                    final List<User> friends = information.getFriends();
                    final String localUserId = Session.getUserId();

                    boolean success = true;
                    for (User friend : friends) {
                        if (!(new UserDAO(friend, localUserId).save(transaction))) {
                            success = false;
                        }
                    }
                    transaction.setSuccessful(success);
                    transaction.finish();
                    return success;
                }

                @Override
                protected void deliverResponse(Boolean result) {
                    BusProvider.getInstance().post(new FriendListRefreshedEvent(result));
                    stopSelf(startId);
                }
            }
        );
        return Service.START_NOT_STICKY;
    }
}
