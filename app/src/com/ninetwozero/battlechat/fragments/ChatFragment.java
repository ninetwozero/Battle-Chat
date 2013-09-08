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

package com.ninetwozero.battlechat.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListFragment;
import com.ninetwozero.battlechat.adapters.MessageListAdapter;
import com.ninetwozero.battlechat.datatypes.Message;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpHeaders;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.misc.Keys;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatFragment extends AbstractListFragment {

    public static final String EXTRA_USER = "user";

    private User mUser;
    private long mChatId = 0;
    private boolean mFirstRun = true;
    private long mLatestMessageTimestamp = 0;

    private LayoutInflater mInflater;
    private ReloadTask mReloadTask;
    private SendMessageTask mSendMessageTask;
    private SharedPreferences mSharedPreferences;
    private Timer mTimer;
    private SoundPool mSoundPool;
    private int mSoundId = 0;

    public ChatFragment() {}

    public static Fragment newInstance() {
        final ChatFragment fragment = new ChatFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle icicle) {
        mInflater = inflater;

        final View view = mInflater.inflate(R.layout.activity_chat, parent, false);
        initialize(view, icicle);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Bundle arguments = getArguments();
        if( arguments.containsKey(ChatFragment.EXTRA_USER)) {
            final User user = arguments.getParcelable(ChatFragment.EXTRA_USER);
            openChatWithUser(user);
        } else if( mUser == null ) {
            startTimer();
            setupSound();
        }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new CloseTask().execute(mChatId);
    }

    private void setChatTitle() {
        if( isAdded() && mUser != null ) {
            getActivity().setTitle(String.format(getString(R.string.text_chat_title), mUser.getUsername()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        stopSound();
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        final MessageListAdapter adapter = (MessageListAdapter) getListAdapter();
        final ArrayList<Message> messages = (ArrayList<Message>) adapter.getItems();

        out.putLong("chatId", mChatId);
        out.putParcelable("user", mUser);
        out.putParcelableArrayList("messages", messages);
        out.putBoolean("firstRun", mFirstRun);

        super.onSaveInstanceState(out);
    }

    private void initialize(final View view, final Bundle icicle) {
        setupForm(view);
        setupFromSavedInstance(icicle);
    }

    private void setupFromSavedInstance(Bundle in) {
        if (in == null) {
            return;
        }

        final long chatId = in.getLong("chatId");
        final User user = in.getParcelable("user");
        final List<Message> messages = in.getParcelableArrayList("messages");
        final MessageListAdapter adapter = (MessageListAdapter) getListAdapter();
        final boolean firstRun = in.getBoolean("firstRun");

        mChatId = chatId;
        mUser = user;
        mFirstRun = firstRun;

        if( adapter != null ) {
            adapter.setItems(messages);
        }
    }

    private void reload(boolean show) {
        if (mReloadTask == null) {
            mReloadTask = new ReloadTask(show);
            mReloadTask.execute();
        }
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
        setListAdapter(new MessageListAdapter(getActivity(), "N/A"));
    }

    private void loadChatIfInitialUserExists() {
        if( mUser == null ) {
            return;
        }
        reload(true);
    }

    private void updateUsernameInAdapter(final String username) {
        ((MessageListAdapter) getListAdapter()).setOtherUser(username);
    }

    private void startTimer() {
        if( mTimer == null ) {
            mTimer = new Timer();
            mTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            reload(false);
                                        }
                                    }
                            );
                        }
                    },
                    0,
                    mSharedPreferences.getInt(Keys.Settings.CHAT_INTERVAL, 25) * 1000 //--> ms
            );
        }
    }

    private void onSend() {
        if (mSendMessageTask != null) {
            showToast(R.string.msg_chat_send_multiple_error);
            return;
        }

        EditText field = (EditText) getView().findViewById(R.id.input_message);
        String message = field.getText().toString();
        if (message.length() == 0) {
            field.setError(getString(R.string.msg_chat_send_message_error));
            field.requestFocus();
            return;
        }

        mSendMessageTask = new SendMessageTask();
        mSendMessageTask.execute(message, String.valueOf(mChatId), BattleChat.getSession().getChecksum());
    }

    private void toggleButton(boolean enable) {
        final Button button = (Button) getView().findViewById(R.id.button_send);
        if (enable) {
            button.setText(R.string.label_send);
            button.setEnabled(true);
        } else {
            button.setText(R.string.label_sending);
            button.setEnabled(false);
        }

    }

    private void clearInput() {
        ((TextView) getView().findViewById(R.id.input_message)).setText("");
    }

    private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
        private List<Message> mMessages = new ArrayList<Message>();
        private boolean mShow = true;

        public ReloadTask(boolean show) {
            mShow = show;
        }

        @Override
        protected void onPreExecute() {
            if (getListView().getCount() == 0 || mShow) {
                toggleLoading(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject result = BattleChatClient.post(
                        HttpUris.Chat.MESSAGES.replace("{USER_ID}", String.valueOf(mUser.getId())),
                        new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
                );

                if (result.has("chatId")) {
                    JSONObject chatObject = result.getJSONObject("chat");
                    mChatId = result.getLong("chatId");
                    mMessages = getMessagesFromJSON(chatObject);
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (shouldNotifyUser(mMessages)) {
                    notifyWithSound();
                }
                ((MessageListAdapter) getListAdapter()).setItems(mMessages);
                scrollToBottom();
            } else {
                showToast(R.string.msg_chat_reload_fail);
                getView().findViewById(R.id.button_send).setEnabled(false);
                stopTimer();
            }
            toggleLoading(false);
            mReloadTask = null;
        }

        public boolean shouldNotifyUser(List<Message> messages) {
            Message message;
            for (int curr = messages.size() - 1, min = (curr > 5 ? curr - 5 : 0); curr > min; curr--) {
                message = messages.get(curr);
                if (message.getTimestamp() < mLatestMessageTimestamp) {
                    return false;
                }

                if (message.getUsername().equals(mUser.getUsername()) && mLatestMessageTimestamp < message.getTimestamp()) {
                    mFirstRun = (mLatestMessageTimestamp == 0);
                    mLatestMessageTimestamp = message.getTimestamp();
                    return true;
                }
            }
            return false;
        }

        private List<Message> getMessagesFromJSON(JSONObject chatObject) throws JSONException {
            List<Message> results = new ArrayList<Message>();
            JSONArray messages = chatObject.getJSONArray("messages");
            JSONObject message;

            for (int i = 0, max = messages.length(); i < max; i++) {
                message = new JSONObject(messages.getString(i));
                results.add(
                        new Message(
                                message.getString("message"),
                                message.getString("fromUsername"),
                                message.getLong("timestamp")
                        )
                );
            }
            return results;
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showToast(R.string.msg_chat_sending_message);
            toggleButton(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                JSONObject result = BattleChatClient.post(
                        HttpUris.Chat.SEND,
                        HttpHeaders.Post.AJAX,
                        new BasicNameValuePair("message", params[0]),
                        new BasicNameValuePair("chatId", params[1]),
                        new BasicNameValuePair("post-check-sum", params[2])
                );
                return !result.has("error");
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                showToast(R.string.msg_message_ok);
                clearInput();
                reload(false);
            } else {
                showToast(R.string.msg_message_fail);
            }
            mSendMessageTask = null;
            toggleButton(true);
        }
    }

    private void notifyWithSound() {
        if (mSharedPreferences.getBoolean(Keys.Settings.BEEP_ON_NEW, true)) {
            playSound();
        }
    }

    public void scrollToBottom() {
        getListView().post(
                new Runnable() {
                    @Override
                    public void run() {
                        getListView().setSelection(getListAdapter().getCount() - 1);
                    }
                }
        );
    }

    private void toggleLoading(boolean isLoading) {
        final View view = getView().findViewById(R.id.status);
        view.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void setupSound() {
        if( mSoundPool == null ) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
            mSoundId = mSoundPool.load(getActivity(), R.raw.notification, 1);
        }
    }

    private void stopSound() {
        if (mSoundPool == null) {
            return;
        }
        mSoundPool.release();
        mSoundPool = null;
        mSoundId = 0;
    }

    private void playSound() {
        if (mSoundPool == null) {
            return;
        }
        mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1);
    }

    private class CloseTask extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... params) {
            try {
                BattleChatClient.get(
                    HttpUris.Chat.CLOSE.replace("{CHAT_ID}", String.valueOf(params[0])),
                    HttpHeaders.Get.AJAX

                );
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void openChatWithUser(final User user) {
        mUser = user;
        if( mUser == null ) {
            throw new IllegalStateException("User is null");
        }

        final View view = getView();
        if( view == null ) {
            return;
        }

        setChatTitle();
        setupListView(getView());

        startTimer();
        setupSound();

        reload(true);
    }
}
