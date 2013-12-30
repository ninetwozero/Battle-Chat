package com.ninetwozero.battlechat.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.factories.GsonFactory;
import com.ninetwozero.battlechat.network.exception.Failure;

public class IntelLoader<T> extends AsyncTaskLoader<Result> {

    private final BaseSimpleRequest request;
    private final Class<T> clazz;

    public IntelLoader(final Context context, final BaseSimpleRequest request) {
        super(context);
        this.request = request;
        this.clazz = null;
    }

    public IntelLoader(final Context context, final BaseSimpleRequest request, final Class<T> clazz) {
        super(context);
        this.request = request;
        this.clazz = clazz;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Result loadInBackground() {
        try {
            return actOn(request.execute());
        } catch (final Failure failure) {
            if (failure.getCause() instanceof HttpRequest.HttpRequestException) {
                return new Result(Result.Status.NETWORK_FAILURE, "Network Failure");
            } else {
                return new Result(Result.Status.FAILURE, "General Error");
            }
        }
    }

    private Result actOn(final String theResult) {
        if (clazz == null) {
            return new Result<T>(Result.Status.SUCCESS, theResult, null);
        }
        final JsonElement jsonRoot = new JsonParser().parse(theResult).getAsJsonObject().get("data");
        return new Result<T>(
            Result.Status.SUCCESS,
            "OK",
            GsonFactory.getInstance().fromJson(
                jsonRoot,
                clazz
            )
        );
    }
}
