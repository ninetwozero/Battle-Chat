package com.ninetwozero.battlechat.base.ui;

import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.factories.GsonProvider;
import com.ninetwozero.battlechat.utils.BusProvider;

public abstract class BaseLoadingListFragment extends BaseListFragment implements Response.ErrorListener {
    protected final Gson gson = GsonProvider.getInstance();

    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public void onStop() {
        BattleChat.getRequestQueue().cancelAll(
            new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return request.getMethod() == Request.Method.GET;
                }
            }
        );
        super.onStop();
    }

    protected void showAsLoading(final boolean show) {
        final View view = getView();
        if (view == null) {
            return;
        }
        view.findViewById(R.id.wrap_loading).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void startLoadingData() {
        reload(true);
    }

    protected abstract void reload(boolean showAsLoading);
}
