package com.ninetwozero.battlechat.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListFragment;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.interfaces.ActivityAccessInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SlidingMenuFragment extends AbstractListFragment {
    private LayoutInflater mInflater;
    private ReloadTask mReloadTask;

    public SlidingMenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle icicle) {
        mInflater = inflater;

        final View view = mInflater.inflate(R.layout.fragment_slidingmenu, parent, false);
        initialize(view, icicle);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(false);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        final UserListAdapter adapter = (UserListAdapter) getListView().getAdapter();
        final ArrayList<User> friends = (ArrayList<User>) adapter.getItems();
        out.putParcelableArrayList("friends", friends);

        super.onSaveInstanceState(out);
    }

    private void initialize(final View view, final Bundle icicle) {
        setupFromSavedInstance(icicle);
        setupListView(view);
    }

    private void setupFromSavedInstance(Bundle in) {
        if( in == null ){
            return;
        }
        final List<User> friends = in.getParcelableArrayList("friends");
        final UserListAdapter adapter = (UserListAdapter) getListAdapter();
        adapter.setItems(friends);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        final User user = (User) view.getTag();
        if( user != null ) {
            FragmentManager manager = getFragmentManager();
            Fragment fragment = manager.findFragmentByTag("ChatFragment");
            if( fragment instanceof ChatFragment ) {
                ((ChatFragment) fragment).openChatWithUser(user);
                toggleSlidingMenu();
            } else {
                showToast("The chat has yet to be loaded.");
            }
            listView.setActivated(true);
        }
    }

    private void setupListView(final View view) {
        ((ListView) view.findViewById(android.R.id.list)).setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(new UserListAdapter(getActivity()));
    }

    private void reload(boolean show) {
        if( mReloadTask == null ) {
            mReloadTask = new ReloadTask(show);
            mReloadTask.execute();
        }
    }

    private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
        private String mMessage;
        private List<User> mItems;
        private boolean mShow;

        public ReloadTask(boolean show) {
            mShow = show;
        }

        @Override
        protected void onPreExecute() {
            if( getListView().getCount() == 0 || mShow ) {
                //toggleLoading(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject result = BattleChatClient.post(
                        HttpUris.Chat.FRIENDS,
                        new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
                );

                if( result.has("error") ) {
                    mMessage = result.getString("error");
                    return false;
                }

                mItems = getUsersFromJson(result);
                return true;
            } catch( Exception ex ) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( result ) {
                ((UserListAdapter) getListAdapter()).setItems(mItems);
            } else {
                showToast(mMessage);
                logout();
            }
            //toggleLoading(false);
            mReloadTask = null;
        }

        private List<User> getUsersFromJson(JSONObject result) throws JSONException {
            final List<User> users = new ArrayList<User>();
            final JSONArray friends = result.getJSONArray("friendscomcenter");
            final int numFriends = friends.length();

            JSONObject friend;
            JSONObject presence;
            int presenceState;

            int numPlaying = 0;
            int numOnline = 0;
            int numOffline = 0;

            if( numFriends > 0 ) {
                for( int i = 0; i < numFriends; i++ ) {
                    friend = friends.optJSONObject(i);
                    presence = friend.getJSONObject("presence");
                    presenceState = getPresenceStateFromJSON(presence);

                    switch( presenceState ) {
                        case User.PLAYING_MP:
                            numPlaying++;
                            break;
                        case User.ONLINE_WEB:
                        case User.AWAY_WEB:
                            numOnline++;
                            break;
                        case User.OFFLINE:
                            numOffline++;
                            break;
                        default:
                            numOffline++;
                            break;
                    }

                    users.add(
                        new User(
                            Long.parseLong(friend.getString("userId")),
                            friend.getString("username"),
                            presenceState
                        )
                    );
                }

                if (numPlaying > 0) {
                    users.add(new User(0, getString(R.string.label_playing), User.PLAYING_MP));
                }

                if (numOnline > 0) {
                    users.add(new User(0, getString(R.string.label_online), User.ONLINE_WEB));
                }

                if (numOffline > 0) {
                    users.add(new User(0, getString(R.string.label_offline), User.OFFLINE));
                }
                Collections.sort(users, new UserComparator());
            }
            return users;
        }

        private int getPresenceStateFromJSON(final JSONObject presence) {
            if( presence.has("isPlaying") ) {
                return User.PLAYING_MP;
            } else if( presence.has("isAway") ) {
                return User.AWAY_WEB;
            } else if( presence.has("isOnline") ) {
                return User.ONLINE_WEB;
            } else {
                return User.OFFLINE;
            }
        }
    }

    private void toggleSlidingMenu() {
        ((ActivityAccessInterface) getActivity()).toggleSlidingMenu();
    }
}
