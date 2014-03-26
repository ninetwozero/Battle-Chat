package com.ninetwozero.battlechat.base.ui;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.factories.GsonProvider;
import com.ninetwozero.battlechat.utils.BusProvider;

public abstract class BaseLoadingListFragment extends BaseListFragment implements Response.ErrorListener {
    protected final Gson gson = GsonProvider.getInstance();
    protected RequestQueue requestQueue;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        requestQueue = Volley.newRequestQueue(activity);
    }

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
        requestQueue.cancelAll(
            new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return request.getMethod() == Request.Method.GET;
                }
            }
        );
        super.onStop();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(getClass().getSimpleName(), "[VolleyError] " + error.getMessage());
    }

    protected <T> T fromJson(final String json, final Class<T> outClass) {
        final JsonObject jsonObject = extractFromJson(json, false);
        return gson.fromJson(jsonObject, outClass);
    }

    protected <T> T fromJson(final String json, final Class<T> outClass, final boolean returnTopLevelJson) {
        final JsonObject jsonObject = extractFromJson(json, returnTopLevelJson);
        return gson.fromJson(jsonObject, outClass);
    }

    protected JsonObject extractFromJson(final String json, boolean returnTopLevel) {
        JsonParser parser = new JsonParser();
        JsonObject topLevel = parser.parse(json).getAsJsonObject();
        return returnTopLevel? topLevel : topLevel.getAsJsonObject("data");
    }

    protected abstract void startLoadingData();
}
