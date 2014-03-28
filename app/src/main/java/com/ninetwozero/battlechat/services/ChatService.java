package com.ninetwozero.battlechat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.database.models.MessageDAO;
import com.ninetwozero.battlechat.datatypes.ChatRefreshedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.GsonProvider;
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

public class ChatService extends Service implements Response.ErrorListener {
    public static final String USER_ID = "userId";

    private final Gson gson = GsonProvider.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final String userId = intent.getStringExtra(USER_ID);
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(
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

                    return new Integer[] { messageCount, unreadMessageCount };
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.w(getClass().getSimpleName(), "Failed to update the chat: " + error.getMessage());
    }

    protected <T> T fromJson(final String json, final Class<T> outClass) {
        final JsonObject jsonObject = extractFromJson(json, false);
        return gson.fromJson(jsonObject, outClass);
    }

    protected JsonObject extractFromJson(final String json, boolean returnTopLevel) {
        JsonParser parser = new JsonParser();
        JsonObject topLevel = parser.parse(json).getAsJsonObject();
        return returnTopLevel? topLevel : topLevel.getAsJsonObject("data");
    }
}
