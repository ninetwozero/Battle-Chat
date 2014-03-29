package com.ninetwozero.battlechat.ui.navigation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseLoadingListFragment;
import com.ninetwozero.battlechat.dao.UserDAO;
import com.ninetwozero.battlechat.datatypes.FriendListRefreshedEvent;
import com.ninetwozero.battlechat.datatypes.NavigationDrawerIsAttachedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.json.chat.Chat;
import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.services.FriendListService;
import com.ninetwozero.battlechat.ui.chat.ChatFragment;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;

public class NavigationDrawerFragment extends BaseLoadingListFragment {
    public static final String TAG = "NavigationDrawerFragment";

    private static final String STATE_SELECTED_ID = "selectedId";

    private boolean isRefreshing;

    private int currentSelectedId = 0;
    private NavigationDrawerCallbacks callbacks;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedState) {
        final View baseView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        initialize(baseView, savedState);
        return baseView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContentLoading();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }

        startLoadingData();
        BusProvider.getInstance().post(new NavigationDrawerIsAttachedEvent());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(STATE_SELECTED_ID, currentSelectedId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(final ListView listView, final View view, final int position, final long id) {
        final UserDAO user = ((NavigationDrawerListAdapter) getListAdapter()).getItem(position);
        if (user == null) {
            return;
        }

        final SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        preferences.putString("recent_chat_userid", user.getId());
        preferences.putString("recent_chat_username", user.getUsername());
        preferences.apply();

        final Bundle data = new Bundle();
        data.putString(Keys.Profile.ID, user.getId());
        data.putString(Keys.Profile.USERNAME, user.getUsername());
        data.putString(Keys.Profile.GRAVATAR_HASH, user.getGravatarHash());

        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_root, ChatFragment.newInstance(data), ChatFragment.TAG);
        transaction.commit();

        callbacks.onNavigationDrawerItemSelected(
            user.getUsername(),
            getString(NavigationDrawerListAdapter.resolveOnlineStatus(user.getPresenceType()))
        );
    }

    @Subscribe
    public void onReceivedRefreshEvent(final TriggerRefreshEvent event) {
        reload(event.getType() == TriggerRefreshEvent.Type.MANUAL);
    }

    @Subscribe
    public void onFriendListRefreshed(final FriendListRefreshedEvent event) {
        showAsLoading(false);
        isRefreshing = false;
    }

    @Subscribe
    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof NoConnectionError) {
            // TODO: Display it in some nice way
        } else {
            showToast(error.getMessage());
        }
        showAsLoading(false);
    }

    @Override
    protected void reload(final boolean show) {
        if (isRefreshing) {
            return;
        }

        showAsLoading(show);
        isRefreshing = true;

        final Intent intent = new Intent(getActivity(), FriendListService.class);
        intent.putExtra(FriendListService.USER_ID, Session.getUserId());
        getActivity().startService(intent);
    }

    private void setupContentLoading() {
        Query.many(
            UserDAO.class,
            "SELECT * " +
                "FROM " + UserDAO.TABLE_NAME + " " +
                "WHERE localUserId = ? " +
                "ORDER BY presence DESC, username COLLATE NOCASE ASC",
            Session.getUserId()
        ).getAsync(
            getLoaderManager(), new ManyQuery.ResultHandler<UserDAO>() {
                @Override
                public boolean handleResult(CursorList<UserDAO> friends) {
                    updateListAdapter(friends);
                    if (friends.size() > 0) {
                        showAsLoading(false);
                    }
                    return true;
                }
            },
            UserDAO.class
        );
    }

    private void initialize(final View view, final Bundle state) {
        setupDataFromState(state);
        setupRegularViews(view);
        setupListView(view);
    }

    private void setupDataFromState(final Bundle state) {
        if (state != null) {
            currentSelectedId = state.getInt(STATE_SELECTED_ID);
        } else {
            currentSelectedId = 0;
        }
    }

    private void setupRegularViews(final View view) {
        ((TextView) view.findViewById(R.id.login_name)).setText(Session.getUsername());
    }

    private void setupListView(final View view) {
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /*
        We can't really open a group chat like with the rest of the chats,
        so we'll see if we keep it this way or just skip them
     */
    private List<User> fetchGroupChatsForComListing(final List<Chat> chats) {
        final List<User> groupChatsForCom = new ArrayList<User>();
        for (Chat chat : chats) {
            if (chat.getMaxSlots() > 2) {
                final String chatId = String.valueOf(chat.getChatId());
                List<String> usernames = new ArrayList<String>();
                for (User userObject : chat.getUsers()) {
                    if (!userObject.getUsername().equals(Session.getUsername())) {
                        usernames.add(userObject.getUsername());
                    }
                }
                groupChatsForCom.add(
                    new User(
                        chatId,
                        StringUtil.join(usernames, ", "),
                        null,
                        PresenceType.GROUP_WEB
                    )
                );
            }
        }
        return groupChatsForCom;
    }

    protected void updateListAdapter(final CursorList<UserDAO> friends) {
        final NavigationDrawerListAdapter adapter = (NavigationDrawerListAdapter) getListAdapter();
        if (adapter == null) {
            setListAdapter(new NavigationDrawerListAdapter(getActivity(), friends));
        } else {
            adapter.setItems(friends);
        }
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(final String title);

        void onNavigationDrawerItemSelected(final String title, final String subtitle);

        boolean isDrawerOpen();
    }
}
