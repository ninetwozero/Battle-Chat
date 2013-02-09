package com.ninetwozero.battlechat.http;

import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class BattleChatClient {
	
	private final static String JSON_ERROR = "{data:{error:'%s'}}";
	private static DefaultHttpClient mHttpClient = HttpClientFactory.getThreadSafeClient();
	
	private BattleChatClient() {}

	public static JSONObject get(final String url, final int headerId) throws JSONException {
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Referer", url);
			
			for( Header header : HttpHeaders.Post.getHeaders(headerId) ) {
	        	httpGet.setHeader(header.getName(), header.getValue());
	        }
			
			return getJsonFromHttpResponse(mHttpClient.execute(httpGet));
		} catch ( Exception ex) {
			ex.printStackTrace();
			return new JSONObject(String.format(JSON_ERROR, ex.getMessage()));
		}
	}
	
	public static JSONObject post(final String url, final int headerId, final NameValuePair... data) throws JSONException {
		try {
			HttpPost httpPost = new HttpPost(url);
	        httpPost.setHeader("Referer", url);
	        
	        for( Header header : HttpHeaders.Post.getHeaders(headerId) ) {
	        	httpPost.setHeader(header.getName(), header.getValue());
	        }
			
	        httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(data), HTTP.UTF_8));
			return getJsonFromHttpResponse(mHttpClient.execute(httpPost));
		} catch( Exception ex ) {
			ex.printStackTrace();
			return new JSONObject(String.format(JSON_ERROR, ex.getMessage()));			
		}
	}

	private static JSONObject getJsonFromHttpResponse(HttpResponse response) throws JSONException {
        try {       
        	HttpEntity httpEntity = response.getEntity();
        	return new JSONObject(EntityUtils.toString(httpEntity));
        } catch( Exception ex ) {
        	ex.printStackTrace();
        	return new JSONObject(String.format(JSON_ERROR, "No content returned from the server."));
        }
	}
}
