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

package com.ninetwozero.battlechat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.base.asynctasks.BaseLoginTask;
import com.ninetwozero.battlechat.base.asynctasks.BaseLogoutTask;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.UserLoginEvent;
import com.ninetwozero.battlechat.datatypes.UserLogoutEvent;
import com.ninetwozero.battlechat.factories.GsonProvider;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.Chat;
import com.ninetwozero.battlechat.json.chat.ComCenterRequest;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.ninetwozero.battlechat.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class BattlelogSessionService extends Service {
    public static final int FLAG_SUCCESS = 0;
    public static final int FLAG_RETRY_LOGIN = 1;
    public static final int ACTION_LOGIN = 0;
    public static final int ACTION_LOGOUT = 1;
    public static final int ACTION_SYNC = 2;

    public static final String INTENT_CALLED_FROM_ACTIVITY = "calledFromActivity";
    public static final String INTENT_ACTION = "serviceAction";
    public static final String INTENT_INPUT_EMAIL = "userEmail";
    public static final String INTENT_INPUT_PASSWORD = "userPassword";
    public static final String TAG = "BattleChatService";

    private boolean originatedFromActivity;
    private int action;
    private String email;
    private String password;
    private SharedPreferences sharedPreferences;

    private int chatInformationStatusCode = -1;
    private BaseLoginTask userLoginTask;

    public static PendingIntent getPendingIntent(final Context c) {
        return PendingIntent.getService(c, 0, getIntent(c), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static Intent getIntent(final Context c) {
        return new Intent(c, BattlelogSessionService.class);
    }

    public static void scheduleRun(final Context c) {
        final AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            DateUtils.HOUR_IN_MILLIS,
            DateUtils.HOUR_IN_MILLIS,
            BattlelogSessionService.getPendingIntent(c.getApplicationContext())
        );
    }

    public static void scheduleRun(final Context c, final long timeInSeconds) {
        final AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            timeInSeconds * 1000,
            timeInSeconds * 1000,
            BattlelogSessionService.getPendingIntent(c.getApplicationContext())
        );
    }

    public static void unscheduleRun(final Context c) {
        final AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(BattlelogSessionService.getPendingIntent(c.getApplicationContext()));
    }

    @Override
    public void onCreate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        setupData(intent);
        load();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private void setupData(final Intent intent) {
        originatedFromActivity = intent.getBooleanExtra(INTENT_CALLED_FROM_ACTIVITY, false);
        action = intent.getIntExtra(INTENT_ACTION, ACTION_SYNC);
        email = intent.getStringExtra(INTENT_INPUT_EMAIL);
        password = intent.getStringExtra(INTENT_INPUT_PASSWORD);

        if (email == null) {
            email = sharedPreferences.getString(Keys.Session.EMAIL, "");
        }

        if (password == null) {
            password = sharedPreferences.getString(Keys.Session.PASSWORD, "");
        }
    }

    private void load() {
        if (!BattleChat.isConnectedToNetwork()) {
            return;
        }

        if (action == ACTION_SYNC) {
            reloadSession();
            BattleChat.getRequestQueue().add(fetchRequestForChatInformation());
        } else if (action == ACTION_LOGIN) {
            userLoginTask = new UserLoginTask(getApplicationContext(), originatedFromActivity);
            userLoginTask.execute(email, password);
        } else if (action == ACTION_LOGOUT) {
            BattleChat.getRequestQueue().add(new UserLogoutTask(getApplicationContext(), originatedFromActivity));
        }
    }

    private SimpleGetRequest<Integer> fetchRequestForChatInformation() {
        return new SimpleGetRequest<Integer>(
            UrlFactory.buildFriendListURL(),
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (chatInformationStatusCode == FLAG_RETRY_LOGIN) {
                        Log.i(TAG, "Our session has expired - trying to login again.");
                        onLoginExpired(email, password);
                    } else {
                        stopServiceAfterFailedRequests();
                    }
                }
            }
        ) {
            @Override
            protected Integer doParse(String json) {
                Log.i(TAG, "Talking to the website...");
                final ComCenterRequest comCenter = createComCenterRequestFromJson(json);
                final List<Chat> unreadChats = findUnreadChats(comCenter.getInformation().getChats());

                Log.d(TAG, "Number of unread chats: " + unreadChats.size());
                for (Chat chat : unreadChats) {
                    logUnreadChatInLogcat(chat);
                }
                chatInformationStatusCode = FLAG_SUCCESS;
                return unreadChats.size();
            }

            private ComCenterRequest createComCenterRequestFromJson(final String json) {
                final Gson gson = GsonProvider.getInstance();
                final JsonParser parser = new JsonParser();
                return gson.fromJson(
                    parser.parse(json).getAsJsonObject().get("data"),
                    ComCenterRequest.class
                );
            }

            private List<Chat> findUnreadChats(final List<Chat> chats) {
                final List<Chat> unreadChats = new ArrayList<Chat>();
                for (Chat chat : chats) {
                    if (chat.getUnreadCount() > 0) {
                        unreadChats.add(chat);
                    }
                }
                return unreadChats;
            }

            private void logUnreadChatInLogcat(final Chat chat) {
                for (User user : chat.getUsers()) {
                    if (!user.getId().equals(Session.getUserId())) {
                        Log.d(
                            TAG,
                            String.format(
                                "Chat with %s has %s unread messages",
                                user.getUsername(),
                                chat.getUnreadCount()
                            )
                        );
                    }
                }
            }

            @Override
            protected void deliverResponse(Integer response) {
                if (chatInformationStatusCode == FLAG_SUCCESS && Session.hasSession()) {
                    onLoginSuccess();
                }
            }
        };
    }

    private void reloadSession() {
        Log.i(TAG, "Grabbing the session details from SharedPreferences...");
        if (!Session.hasSession()) {
            Log.i(TAG, "We don't have a session. Reloading it...");
            Session.loadSession(sharedPreferences);
        }
    }

    private void stopServiceAfterFailedRequests() {
        Log.i(TAG, "Our session has expired - removing the stored information.");
        BattlelogSessionService.unscheduleRun(getApplicationContext());
        Session.clearSession(getApplicationContext());
        showNotification(false);
        stopSelf();
    }

    private void showNotification(final boolean hasActiveSession) {
        if (sharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, false)) {
            if (hasActiveSession ) {
                NotificationHelper.showLoggedInNotification(getApplicationContext());
            } else {
                NotificationHelper.showLoggedOutNotification(getApplicationContext());
            }
        }
    }

    private void onLoginSuccess() {
        Log.i(TAG, "Our session is valid - no further action required.");
        showNotification(true);
        stopSelf();
    }

    private void onLoginExpired(final String email, final String password) {
        userLoginTask = new UserLoginTask(getApplicationContext(), originatedFromActivity);
        userLoginTask.execute(email, password);
    }

    private class UserLoginTask extends BaseLoginTask {
        private final boolean calledFromActivity;

        public UserLoginTask(final Context context, final boolean calledFromActivity) {
            super(context);
            this.calledFromActivity = calledFromActivity;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userLoginTask = null;
            if (calledFromActivity) {
                BusProvider.getInstance().post(new UserLoginEvent(success, message));
            } else {
                if (success) {
                    Log.i(TAG, "Successfully logged in to the services.");
                    onLoginSuccess();
                } else {
                    Log.i(TAG, "Unable to log in to the services.");
                    stopServiceAfterFailedRequests();
                }
            }
        }

    }

    private class UserLogoutTask extends BaseLogoutTask {
        private final boolean calledFromActivity;

        public UserLogoutTask(final Context context, final boolean calledFromActivity) {
            super(context);
            this.calledFromActivity = calledFromActivity;
        }

        @Override
        protected void deliverResponse(Object response) {
            super.deliverResponse(response);

            if (calledFromActivity) {
                BusProvider.getInstance().post(new UserLogoutEvent());
            } else {
                Log.i(TAG, "Logged out from the services.");
                stopSelf();
            }
        }
    }
}