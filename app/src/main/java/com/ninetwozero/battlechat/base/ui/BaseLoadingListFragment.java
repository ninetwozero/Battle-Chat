package com.ninetwozero.battlechat.base.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ninetwozero.battlechat.network.Result;

public abstract class BaseLoadingListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Result> {
    @Override
    public abstract Loader<Result> onCreateLoader(final int id, final Bundle args);

    @Override
    public void onLoadFinished(final Loader<Result> loader, final Result data) {
        if (data.getStatus() == Result.Status.SUCCESS) {
            onLoadSuccess(loader.getId(), data);
        } else {
            onLoadFailed(loader.getId(), data);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Result> loader) {
    }

    protected void onLoadFailed(final int loader, final Result result) {
        Log.e(getClass().getSimpleName(), "[onLoadFailed] result[" + loader + "] = " + result);
    }

    protected abstract void onLoadSuccess(final int loader, final Result result);
}
