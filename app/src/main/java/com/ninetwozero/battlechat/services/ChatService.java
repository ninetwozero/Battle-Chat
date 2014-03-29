package com.ninetwozero.battlechat.services;

import android.app.Service;
import android.content.Intent;

import com.ninetwozero.battlechat.base.BaseApiService;
import com.ninetwozero.battlechat.dao.MessageDAO;
import com.ninetwozero.battlechat.datatypes.ChatRefreshedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.Chat;
import com.ninetwozero.battlechat.json.chat.ChatContainer;
import com.ninetwozero.battlechat.json.chat.Message;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.utils.BusProvider;

import java.util.List;

import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.Transaction;

public class ChatService extends BaseApiService {

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        requestQueue.add(
            new SimpleGetRequest<Integer[]>(
                UrlFactory.buildOpenChatURL(userId),
                this
            ) {
                private long chatId;
                private User user;

                @Override
                protected Integer[] doParse(String json) {
                    final Transaction transaction = new Transaction();
                    final ChatContainer container = fromJson(json, ChatContainer.class);
                    final Chat chat = container.getChat();
                    final List<Message> messages = chat.getMessages();
                    final MessageDAO latestMessage = Query.one(
                        MessageDAO.class,
                        "SELECT * " +
                        "FROM " + MessageDAO.TABLE_NAME + " " +
                        "WHERE userId = ? " +
                        "ORDER BY timestamp DESC",
                        userId
                    ).get();
                    final long timestamp = latestMessage == null ? 0 : latestMessage.getTimestamp();

                    int messageCount = 0;
                    int unreadMessageCount = 0;
                    boolean success = true;
                    for (Message message : messages) {
                        if (message.getTimestamp() > timestamp) {
                            if (!(new MessageDAO(message, userId).save(transaction))) {
                                success = false;
                            }
                            messageCount++;

                            if (!message.getUsername().equalsIgnoreCase(Session.getUsername())) {
                                unreadMessageCount++;
                            }
                        }
                    }
                    transaction.setSuccessful(success);
                    transaction.finish();

                    chatId = chat.getChatId();
                    for (User chatUser : chat.getUsers()) {
                        if (!chatUser.getId().equals(Session.getUserId())) {
                            user = chatUser;
                            break;
                        }
                    }

                    return new Integer[]{messageCount, unreadMessageCount};
                }

                @Override
                protected void deliverResponse(Integer[] messageCount) {
                    BusProvider.getInstance().post(
                        new ChatRefreshedEvent(
                            chatId,
                            user,
                            messageCount[0],
                            messageCount[1]
                        )
                    );
                    stopSelf(startId);
                }
            }
        );
        return Service.START_NOT_STICKY;
    }
}
