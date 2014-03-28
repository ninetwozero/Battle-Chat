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

package com.ninetwozero.battlechat.ui.chat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseLoadingListFragment;
import com.ninetwozero.battlechat.database.models.MessageDAO;
import com.ninetwozero.battlechat.datatypes.ChatRefreshedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.network.SimplePostRequest;
import com.ninetwozero.battlechat.services.ChatService;
import com.ninetwozero.battlechat.ui.navigation.NavigationDrawerListAdapter;
import com.squareup.otto.Subscribe;

import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.SprinklesContentObserver;

public class ChatFragment extends BaseLoadingListFragment {
    public static final String TAG = "ChatListFragment";

    private static final String KEY_DISPLAY_OVERLAY = "showProgress";
    private static final int ID_REQUEST_SEND = 3100;
    private static final int ID_REQUEST_CLOSE = 3200;

    private long chatId;
    private User user;

    private int soundId;
    private SoundPool soundPool;
    private boolean isRefreshing;

    public ChatFragment() {
    }

    public static Fragment newInstance(final Bundle data) {
        final ChatFragment fragment = new ChatFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle icicle) {
        super.onCreateView(inflater, parent, icicle);

        final View view = layoutInflater.inflate(R.layout.fragment_chat, parent, false);
        initialize(view, icicle);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        handleArgumentsOnResume(getArguments());
        loadFromDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSound();
    }

    @Override
    public void onStop() {
        requestQueue.add(fetchRequestForChatClose(getBundleForClose(chatId)));
        super.onStop();
    }

    @Override
    protected void startLoadingData() {
        if (!isRefreshing) {
            reload(true);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle out) {
        out.putLong(Keys.Chat.CHAT_ID, chatId);
        out.putString(Keys.Profile.ID, user.getId());
        out.putString(Keys.Profile.USERNAME, user.getId());
        out.putString(Keys.Profile.GRAVATAR_HASH, user.getId());

        super.onSaveInstanceState(out);
    }

    @Subscribe
    public void onUserSelected(final User user) {
        if (this.user.getId().equals(user.getId())) {
            return;
        }

        this.user = user;
        if (this.user == null) {
            throw new IllegalStateException("User is null");
        }

        final View view = getView();
        if (view == null) {
            return;
        }

        updateActionBarWithPresence(user);
        setupSound();
        reload(true);
    }

    @Subscribe
    public void onReceivedRefreshEvent(final TriggerRefreshEvent event) {
        if (chatId > 0) {
            reload(event.getType() == TriggerRefreshEvent.Type.MANUAL);
        }
    }

    @Subscribe
    public void onChatRefreshed(ChatRefreshedEvent event) {
        toggleLoading(false);

        if (event.getChatId() != chatId && chatId != 0) {
            doRequest(ID_REQUEST_CLOSE, getBundleForClose(chatId));
        }

        chatId = event.getChatId();
        user = event.getUser();
        updateActionBarWithPresence(user);

        if (event.getUnreadCount() > 0) {
            notifyWithSound();
        }

        isRefreshing = false;
    }

    private void initialize(final View view, final Bundle state) {
        setupFromState(state);
        setupForm(view);
        setupListView(view);
        setupContentObserver();
    }

    private void setupFromState(final Bundle state) {
        if (state == null) {
            return;
        }
        this.chatId = state.getLong(Keys.Chat.CHAT_ID, 0L);
        this.user = new User(
            state.getString(Keys.Profile.ID),
            state.getString(Keys.Profile.USERNAME),
            state.getString(Keys.Profile.GRAVATAR_HASH)
        );

    }

    private void setupForm(final View view) {
        ((EditText) view.findViewById(R.id.input_message)).setOnEditorActionListener(
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_SEND) {
                        onSend();
                        return true;
                    }
                    return false;
                }
            }
        );
        view.findViewById(R.id.button_send).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSend();
                }
            }
        );
    }

    private void setupListView(final View view) {
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    private void setupContentObserver() {
        new SprinklesContentObserver(
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    Log.d("YOLO", "New data in DB --> UPDATE!!!");
                    loadFromDatabase();
                }
            }
        ).register(MessageDAO.class, true);
    }

    private void reload(final boolean show) {
        toggleLoading(show);
        isRefreshing = true;

        final Intent intent = new Intent(getActivity(), ChatService.class);
        intent.putExtra(ChatService.USER_ID, user.getId());
        getActivity().startService(intent);
    }

    private void loadFromDatabase() {
        final CursorList<MessageDAO> messages = Query.many(
            MessageDAO.class,
            "SELECT * " +
            "FROM " + MessageDAO.TABLE_NAME + " " +
            "WHERE userId = ?",
            user.getId()
        ).get();

        final boolean shouldScroll = shouldScrollToLatestMessage();
        updateListAdapter(user, messages);
        if (shouldScroll) {
            scrollToLatestMessage();
        }
    }

    private void doRequest(final int id, final Bundle args) {
        if (id == ID_REQUEST_SEND) {
            toggleButton(false);
            requestQueue.add(fetchRequestForSend(args));
        } else if (id == ID_REQUEST_CLOSE) {
            requestQueue.add(fetchRequestForChatClose(args));
        } else {
            reload(args.getBoolean(KEY_DISPLAY_OVERLAY, true));
        }
    }

    private Request<Object> fetchRequestForSend(Bundle args) {
        return new SimplePostRequest<Object>(
            UrlFactory.buildPostToChatURL(),
            args,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ChatFragment.this.onErrorResponse(error);
                    showToast(R.string.msg_chat_send_message_error);
                }
            }
        ) {
            @Override
            protected Object doParse(String json) {
                return json;
            }

            @Override
            protected void deliverResponse(Object response) {
                clearInput();
                toggleButton(true);
                reload(false);
            }
        };
    }

    private Request<Object> fetchRequestForChatClose(Bundle args) {
        return new SimplePostRequest<Object>(
            UrlFactory.buildCloseChatURL(chatId),
            args,
            this
        ) {
            @Override
            protected Object doParse(String json) {
                return json;
            }

            @Override
            protected void deliverResponse(Object response) {

            }
        };
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        showToast(error.getMessage());
    }

    private boolean shouldScrollToLatestMessage() {
        final ListView listView = getListView();
        final int lastVisiblePosition = listView.getLastVisiblePosition();
        final int maxPosition = listView.getCount() - 1;
        return maxPosition == 0 || lastVisiblePosition == maxPosition;
    }

    private Bundle getBundleForSend(final long chatId, final String message) {
        final Bundle bundle = new Bundle();
        bundle.putString(Keys.Chat.MESSAGE, message);
        bundle.putLong(Keys.Chat.CHAT_ID, chatId);
        bundle.putString(Keys.Chat.CHECKSUM, Session.getChecksum());
        return bundle;
    }

    private Bundle getBundleForClose(final long chatId) {
        final Bundle bundle = new Bundle();
        bundle.putLong(Keys.Chat.CHAT_ID, chatId);
        bundle.putString(Keys.Chat.CHECKSUM, Session.getChecksum());
        return bundle;
    }

    private void handleArgumentsOnResume(final Bundle data) {
        if (data == null) {
            return;
        }

        user = new User(
            data.getString(Keys.Profile.ID),
            data.getString(Keys.Profile.USERNAME),
            data.getString(Keys.Profile.GRAVATAR_HASH)
        );
        updateActionBarWithPresence(user);
    }

    private void onSend() {
        final EditText field = (EditText) getView().findViewById(R.id.input_message);
        final String message = field.getText().toString();
        if (message.length() == 0) {
            field.setError(getString(R.string.msg_chat_send_message_error));
            field.requestFocus();
            return;
        }
        doRequest(ID_REQUEST_SEND, getBundleForSend(chatId, message));
    }

    private void toggleButton(final boolean enable) {
        final View button = getView().findViewById(R.id.button_send);
        button.setEnabled(enable);
        button.setAlpha(enable ? 1.0f : 0.5f);
    }

    private void clearInput() {
        final View view = getView();
        if (view == null) {
            return;
        }

        final EditText input = (EditText) view.findViewById(R.id.input_message);
        input.setText("");
        input.setError(null);
    }

    private void notifyWithSound() {
        if (sharedPreferences.getBoolean(Keys.Settings.BEEP_ON_NEW, true)) {
            playSound();
        }
    }

    private void toggleLoading(boolean isLoading) {
        final View view = getView();
        if (view == null) {
            return;
        }
        view.findViewById(R.id.status).setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void setupSound() {
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
            soundId = soundPool.load(getActivity(), R.raw.notification, 1);
        }
    }

    private void playSound() {
        if (soundPool == null) {
            return;
        }
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1);
    }

    private void stopSound() {
        if (soundPool == null) {
            return;
        }
        soundPool.release();
        soundPool = null;
        soundId = 0;
    }

    protected void updateListAdapter(final User user, final CursorList<MessageDAO> messages) {
        RegularChatListAdapter adapter = (RegularChatListAdapter) getListAdapter();
        if (adapter == null) {
            adapter = new RegularChatListAdapter(getActivity(), Session.getUsername(), messages);
            setListAdapter(adapter);
        } else {
            adapter.setItems(messages);
        }
        adapter.setUser(user);
    }

    private void updateActionBarWithPresence(final User user) {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(user.getUsername());
        actionBar.setSubtitle(NavigationDrawerListAdapter.resolveOnlineStatus(user.getPresenceType()));
    }

    public void scrollToLatestMessage() {
        if (getView() == null) {
            return;
        }

        final ListView listView = getListView();
        listView.post(
            new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(getListAdapter().getCount() - 1);
                }
            }
        );
    }
}
