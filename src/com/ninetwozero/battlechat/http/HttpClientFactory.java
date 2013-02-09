package com.ninetwozero.battlechat.http;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class HttpClientFactory {

	private static DefaultHttpClient mHttpClient;

	private HttpClientFactory() {}
	public static DefaultHttpClient getThreadSafeClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			ClientConnectionManager mgr = mHttpClient.getConnectionManager();
			HttpParams params = mHttpClient.getParams();
			mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
		}			
		return mHttpClient;
	}
}
