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

public class BattleChatClient {
	
	private static DefaultHttpClient mHttpClient = HttpClientFactory.getThreadSafeClient();
	
	private BattleChatClient() {}

	
	public static String get(final String url) {
		return get(url, HttpHeaders.Get.NORMAL);
	}
	
	public static String get(final String url, final int headerId) {
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Referer", url);
			
			for( Header header : HttpHeaders.Post.getHeaders(headerId) ) {
	        	httpGet.setHeader(header.getName(), header.getValue());
	        }
			
			return getStringFromHttpResponse(mHttpClient.execute(httpGet));
		} catch ( Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String post(final String url, final NameValuePair... data) {
		return post(url, HttpHeaders.Post.NORMAL, data);
	}
	
	public static String post(final String url, final int headerId, final NameValuePair... data) {
		try {
			HttpPost httpPost = new HttpPost(url);
	        httpPost.setHeader("Referer", url);
	        
	        for( Header header : HttpHeaders.Post.getHeaders(headerId) ) {
	        	httpPost.setHeader(header.getName(), header.getValue());
	        }
			
	        httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(data), HTTP.UTF_8));
			return getStringFromHttpResponse(mHttpClient.execute(httpPost));
		} catch( Exception ex ) {
			ex.printStackTrace();
			return "";			
		}
	}

	public static String getStringFromHttpResponse(HttpResponse response) {
        try {       
        	HttpEntity httpEntity = response.getEntity();
        	if( httpEntity.getContentLength() > 0 ) {
        		return EntityUtils.toString(httpEntity);
        	}
        } catch( Exception ex ) {
        	ex.printStackTrace();
        }
        return "";
	}
}
