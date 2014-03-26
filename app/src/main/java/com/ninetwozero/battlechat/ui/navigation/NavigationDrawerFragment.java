package com.ninetwozero.battlechat.ui.navigation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseLoadingListFragment;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.NavigationDrawerIsAttachedEvent;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.Chat;
import com.ninetwozero.battlechat.json.chat.ComCenterInformation;
import com.ninetwozero.battlechat.json.chat.ComCenterRequest;
import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.ui.chat.ChatFragment;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigationDrawerFragment extends BaseLoadingListFragment {
    public static final String TAG = "NavigationDrawerFragment";

    private static final int ID_LOADER = 2000;
    private static final String STATE_SELECTED_ID = "selectedId";
    private static final String KEY_DISPLAY_OVERLAY = "manual_reload";

    private boolean shouldReloadOnAttach = false;
    private boolean shouldReloadWithLoadingScreen = false;
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
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }

        if (shouldReloadOnAttach) {
            shouldReloadWithLoadingScreen = false;
            shouldReloadOnAttach = false;
            reload(false);
        }

        BusProvider.getInstance().post(new NavigationDrawerIsAttachedEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        startLoadingData();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        final LoaderManager manager = getLoaderManager();
        if (manager != null && manager.hasRunningLoaders()) {
            manager.destroyLoader(ID_LOADER);
        }

        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(STATE_SELECTED_ID, currentSelectedId);
        super.onSaveInstanceState(outState);
    }

    // TODO: Load friends from DB && selectItemFromState(currentSelectedId); ?
    @Override
    protected void startLoadingData() {
        doLoadData(new Bundle());
    }

    private void doLoadData(final Bundle arguments) {
        final ListView listView = getListView();
        if (listView == null || listView.getCount() == 0 || arguments.getBoolean(KEY_DISPLAY_OVERLAY)) {
            showProgress(true);
        }
        requestQueue.add(
            new SimpleGetRequest<List<User>>(
                UrlFactory.buildFriendListURL(),
                this
            ) {
                @Override
                protected List<User> doParse(String json) {
                    final ComCenterRequest comCenter = fromJson(json, ComCenterRequest.class);
                    final ComCenterInformation comCenterInformation = comCenter.getInformation();
                    if (comCenterInformation == null) {
                        return null;
                    }

                    final List<User> friends = comCenterInformation.getFriends();
                    if (friends == null) {
                        return null;
                    }
                    Collections.sort(friends, new UserComparator());
                    return friends;
                }

                @Override
                protected void deliverResponse(List<User> response) {
                    updateListAdapter(response);
                    showProgress(false);
                }
            }
        );
    }

    @Override
    public void onListItemClick(final ListView listView, final View view, final int position, final long id) {
        final User user = ((NavigationDrawerListAdapter) getListAdapter()).getItem(position);
        if (user == null) {
            return;
        }

        final SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        preferences.putString("recent_chat_userid", user.getId());
        preferences.putString("recent_chat_username", user.getUsername());
        preferences.apply();

        final FragmentManager manager = getFragmentManager();
        final Fragment fragment = manager.findFragmentByTag(ChatFragment.TAG);
        if (fragment == null) {
            final Bundle data = new Bundle();
            data.putString(Keys.Profile.ID, user.getId());
            data.putString(Keys.Profile.USERNAME, user.getUsername());
            data.putString(Keys.Profile.GRAVATAR_HASH, user.getGravatarHash());

            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_root, ChatFragment.newInstance(data), ChatFragment.TAG);
            transaction.commit();
        } else {
            BusProvider.getInstance().post(user);
        }

        callbacks.onNavigationDrawerItemSelected(
            user.getUsername(),
            getString(NavigationDrawerListAdapter.resolveOnlineStatus(user.getPresenceType()))
        );
    }

    @Subscribe
    public void onReceivedRefreshEvent(final TriggerRefreshEvent event) {
        reload(event.getType() == TriggerRefreshEvent.Type.MANUAL);
    }

    private void reload(final boolean shouldDisplayLoadingOverlay) {
        if (getActivity() == null) {
            shouldReloadOnAttach = true;
            shouldReloadWithLoadingScreen = shouldDisplayLoadingOverlay;
            return;
        }

        final Bundle arguments = getArguments() == null ? new Bundle() : getArguments();
        arguments.putBoolean(KEY_DISPLAY_OVERLAY, shouldDisplayLoadingOverlay);
        doLoadData(arguments);
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

    private void showProgress(final boolean show) {
        final View view = getView();
        if (view == null) {
            return;
        }
        view.findViewById(R.id.wrap_loading).setVisibility(show ? View.VISIBLE : View.GONE);
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

    protected void updateListAdapter(final List<User> friends) {
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
