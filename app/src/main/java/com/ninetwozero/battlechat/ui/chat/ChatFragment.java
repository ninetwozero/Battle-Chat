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
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseLoadingListFragment;
import com.ninetwozero.battlechat.datatypes.NoResultExpected;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.Chat;
import com.ninetwozero.battlechat.json.chat.ChatContainer;
import com.ninetwozero.battlechat.json.chat.Message;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.network.IntelLoader;
import com.ninetwozero.battlechat.network.Result;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.network.SimplePostRequest;
import com.ninetwozero.battlechat.ui.navigation.NavigationDrawerListAdapter;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.List;

public class ChatFragment extends BaseLoadingListFragment {
    public static final String TAG = "ChatListFragment";

    private static final String KEY_DISPLAY_OVERLAY = "showProgress";
    private static final int ID_LOADER_REFRESH = 3000;
    private static final int ID_LOADER_SEND = 3100;
    private static final int ID_LOADER_CLOSE = 3200;

    private User user;
    private long chatId;
    private SharedPreferences sharedPreferences;
    private SoundPool soundPool;
    private int soundId;

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
        BusProvider.getInstance().register(this);
        handleArgumentsOnResume(getArguments());
        initialLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        stopSound();
    }

    @Override
    public void onStop() {
        getLoaderManager().restartLoader(ID_LOADER_CLOSE, getBundleForClose(chatId), this);
        super.onStop();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onSaveInstanceState(final Bundle out) {
        out.putLong(Keys.Chat.CHAT_ID, chatId);
        out.putString(Keys.Profile.ID, user.getId());
        out.putString(Keys.Profile.USERNAME, user.getId());
        out.putString(Keys.Profile.GRAVATAR_HASH, user.getId());

        super.onSaveInstanceState(out);
    }

    @Override
    public Loader<Result> onCreateLoader(final int id, final Bundle args) {
        if (id == ID_LOADER_SEND) {
            toggleButton(false);
            return new IntelLoader<NoResultExpected>(
                getActivity(),
                new SimplePostRequest(UrlFactory.buildPostToChatURL(), args)
            );
        } else if (id == ID_LOADER_CLOSE) {
            return new IntelLoader<NoResultExpected>(
                getActivity(),
                new SimpleGetRequest(UrlFactory.buildCloseChatURL(chatId))
            );
        } else {
            toggleLoading(args.getBoolean(KEY_DISPLAY_OVERLAY, true));
            return new IntelLoader<ChatContainer>(
                getActivity(),
                new SimplePostRequest(
                    UrlFactory.buildOpenChatURL(args.getString(Keys.Profile.ID)),
                    args
                ),
                ChatContainer.class
            );
        }
    }

    @Override
    protected void onLoadFailed(final int id, final Result result) {
        if (id == ID_LOADER_SEND) {
            showToast(R.string.msg_chat_send_message_error);
        } else if (id == ID_LOADER_REFRESH) {
            showToast(R.string.msg_chat_load_error);
        }
    }

    @Override
    protected void onLoadSuccess(final int id, final Result result) {
        if (id == ID_LOADER_SEND) {
            getLoaderManager().destroyLoader(id);
            clearInput();
            toggleButton(true);
            reload(false);
        } else if (id == ID_LOADER_REFRESH) {
            onSuccessfulRefresh(result);
        }
    }

    private void onSuccessfulRefresh(final Result result) {
        final ChatContainer container = (ChatContainer) result.getData();
        final Chat chat = container.getChat();
        if (chat == null) {
            showToast(R.string.msg_chat_refresh_fail);
            return;
        }

        if (chat.getChatId() != chatId && chatId != 0) {
            getLoaderManager().restartLoader(ID_LOADER_CLOSE, getBundleForClose(chatId), this);
        }

        this.chatId = chat.getChatId();
        for (User user : chat.getUsers()) {
            if (!user.getId().equals(Session.getUserId())) {
                this.user = user;
                break;
            }
        }

        if (chat.getUnreadCount() > 0) {
            notifyWithSound();
        }

        final boolean doScroll = shouldScrollToLatestMessage();
        updateListAdapter(user, chat.getMessages());
        updateActionBarWithPresence(user);
        if (doScroll) {
            scrollToLatestMessage();
        }
        toggleLoading(false);
    }

    private boolean shouldScrollToLatestMessage() {
        final ListView listView = getListView();
        final int lastVisiblePosition = listView.getLastVisiblePosition();
        final int maxPosition = listView.getCount() - 1;
        return maxPosition == 0 || lastVisiblePosition == maxPosition;
    }

    @Subscribe
    public void onUserSelected(final User user) {
        this.user = user;
        if (this.user == null) {
            throw new IllegalStateException("User is null");
        }

        final View view = getView();
        if (view == null) {
            return;
        }

        setupSound();
        reload(true);
    }

    @Subscribe
    public void onReceivedRefreshEvent(final TriggerRefreshEvent event) {
        if (chatId > 0) {
            reload(event.getType() == TriggerRefreshEvent.Type.MANUAL);
        }
    }

    private void initialize(final View view, final Bundle state) {
        setupFromState(state);
        setupForm(view);
        setupListView(view);
    }

    private void setupFromState(final Bundle state) {
        if (state == null) {
            return;
        }
        this.chatId = state.getLong(Keys.Chat.CHAT_ID);
        this.user = new User(
            state.getString(Keys.Profile.ID),
            state.getString(Keys.Profile.USERNAME),
            state.getString(Keys.Profile.GRAVATAR_HASH)
        );

    }

    private void initialLoad() {
        final LoaderManager manager = getLoaderManager();
        final boolean shouldDisplayLoader = manager.getLoader(ID_LOADER_REFRESH) == null;
        manager.initLoader(ID_LOADER_REFRESH, getBundleForRefresh(shouldDisplayLoader), this);
    }

    private void reload(final boolean show) {
        getLoaderManager().restartLoader(ID_LOADER_REFRESH, getBundleForRefresh(show), this);
    }

    private Bundle getBundleForRefresh(final boolean showProgress) {
        final Bundle bundle = new Bundle();
        bundle.putString(Keys.Profile.ID, user.getId());
        bundle.putString(Keys.Chat.CHECKSUM, Session.getChecksum());
        bundle.putBoolean(KEY_DISPLAY_OVERLAY, showProgress);
        return bundle;
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
    }

    private void setupForm(final View view) {
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

    private void onSend() {
        final EditText field = (EditText) getView().findViewById(R.id.input_message);
        final String message = field.getText().toString();
        if (message.length() == 0) {
            field.setError(getString(R.string.msg_chat_send_message_error));
            field.requestFocus();
            return;
        }
        getLoaderManager().restartLoader(ID_LOADER_SEND, getBundleForSend(chatId, message), this);
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

    private void stopSound() {
        if (soundPool == null) {
            return;
        }
        soundPool.release();
        soundPool = null;
        soundId = 0;
    }

    private void playSound() {
        if (soundPool == null) {
            return;
        }
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1);
    }

    protected void updateListAdapter(final User user, final List<Message> messages) {
        MessageListAdapter adapter = (MessageListAdapter) getListAdapter();
        if (adapter == null) {
            adapter = new MessageListAdapter(getActivity(), Session.getUsername(), messages);
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
