package com.ninetwozero.battlechat.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.factories.GsonProvider;
import com.ninetwozero.battlechat.utils.BusProvider;

public abstract class BaseApiService extends Service implements Response.ErrorListener {
    public static final String USER_ID = "userId";

    protected String userId;
    private final Gson gson = GsonProvider.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        userId = intent.getStringExtra(USER_ID);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        BusProvider.getInstance().post(error);
    }

    protected <T> T fromJson(final String json, final Class<T> outClass) {
        final JsonObject jsonObject = extractFromJson(json, false);
        return gson.fromJson(jsonObject, outClass);
    }

    protected JsonObject extractFromJson(final String json, boolean returnTopLevel) {
        JsonParser parser = new JsonParser();
        JsonObject topLevel = parser.parse(json).getAsJsonObject();
        return returnTopLevel ? topLevel : topLevel.getAsJsonObject("data");
    }
}
