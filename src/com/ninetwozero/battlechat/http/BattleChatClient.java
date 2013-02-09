package com.ninetwozero.battlechat.http;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class BattleChatClient {
	
	private final static String JSON_ERROR = "{data:{error:'%s'}}";
	private static DefaultHttpClient mHttpClient = HttpClientFactory.getThreadSafeClient();
	
	private BattleChatClient() {}

	public static JSONObject get(final String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		return getJsonFromHttpResponse(mHttpClient.execute(httpGet));
	}
	
	public static JSONObject post(final String url, final NameValuePair... postData) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		return getJsonFromHttpResponse(mHttpClient.execute(httpPost));
	}

	private static JSONObject getJsonFromHttpResponse(HttpResponse response) throws IOException, JSONException {
        HttpEntity httpEntity = response.getEntity();
    	if (httpEntity == null) {
        	return new JSONObject(String.format(JSON_ERROR, "No content returned from the server."));
        } else {
            return new JSONObject(EntityUtils.toString(httpEntity));
        }
	}
}
