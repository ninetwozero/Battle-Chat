package com.ninetwozero.battlechat.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListFragment;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.interfaces.ActivityAccessInterface;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        out.putBoolean("showingSlidingMenu", isMenuShowing());

        super.onSaveInstanceState(out);
    }

    private void initialize(final View view, final Bundle icicle) {
        setupFromSavedInstance(icicle);
        setupAccountBox(view);
        setupListView(view);
    }

    private void setupFromSavedInstance(Bundle in) {
        if( in == null ){
            return;
        }

        final List<User> friends = in.getParcelableArrayList("friends");
        final boolean show = in.getBoolean("showingSlidingMenu");
        final UserListAdapter adapter = (UserListAdapter) getListAdapter();
        if( adapter != null ) {
            adapter.setItems(friends);
        }
        toggleSlidingMenu(show);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        final User user = ((UserListAdapter) getListAdapter()).getItem(position);
        if( user != null ) {

            final FragmentManager manager = getFragmentManager();
            final Fragment fragment = manager.findFragmentByTag("ChatFragment");
            if( fragment == null ) {
                final Bundle arguments = new Bundle();
                final Fragment newFragment = ChatFragment.newInstance();
                final FragmentTransaction transaction = manager.beginTransaction();

                arguments.putParcelable(ChatFragment.EXTRA_USER, user);
                newFragment.setArguments(arguments);

                transaction.replace(android.R.id.content, newFragment, "ChatFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                toggleSlidingMenu();
            } else {
                if( fragment instanceof ChatFragment ) {
                    ((ChatFragment) fragment).openChatWithUser(user);
                    toggleSlidingMenu();
                }
            }
            final SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            preferences.putLong("recent_chat_userid", user.getId());
            preferences.putString("recent_chat_username", user.getUsername());
            preferences.commit();
        }
    }

    private void setupAccountBox(final View view) {
        ((TextView) view.findViewById(R.id.user_account)).setText(BattleChat.getSession().getUsername());
        ((TextView) view.findViewById(R.id.user_email)).setText(BattleChat.getSession().getEmail());
        ((ImageView) view.findViewById(R.id.gravatar)).setImageURI(
            BattleChat.getFileUri(BattleChat.getSession().getUserId())
        );
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
                toggleLoading(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                final JSONObject result = BattleChatClient.post(
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
            toggleLoading(false);
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

    private void toggleLoading(final boolean show) {
        final View view = getView();
        if( view != null ) {
            view.findViewById(R.id.status).setVisibility(show? View.VISIBLE : View.GONE);
        }
    }

    private void toggleSlidingMenu() {
        ((ActivityAccessInterface) getActivity()).toggleSlidingMenu();
    }

    private void toggleSlidingMenu(final boolean show) {
        ((ActivityAccessInterface) getActivity()).toggleSlidingMenu(show);
    }

    private boolean isMenuShowing() {
        return ((ActivityAccessInterface) getActivity()).isMenuShowing();
    }
}
