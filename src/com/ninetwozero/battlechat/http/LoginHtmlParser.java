package com.ninetwozero.battlechat.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

final public class LoginHtmlParser {

	private final Document mDocument;
	private final String mHtml;
	
	public LoginHtmlParser(final String html) {
		mHtml = html;
		mDocument = Jsoup.parse(mHtml);
	}
	
	public long getUserId() {
		String id = mDocument.select(".base-header-profile-dropdown-username .base-avatar-size-large").attr("rel");
		return Long.parseLong(id);
	}
	
	public String getUsername() {
		return mDocument.select(".base-header-profile-dropdown-username span").text();
	}
	
	public String getChecksum() {
		return mDocument.select("#base-header-search-area input[name=post-check-sum]").val();
	}
	
	public boolean hasErrorMessage() {
		Elements error = mDocument.select(".gate-login-errormsg.wfont");
		if( error.isEmpty() ) {
			return false;
		} else {
			System.out.println(error.text());
			return error.hasText();
		}
	}

	public String getErrorMessage() {
		return mDocument.select(".gate-login-errormsg.wfont").first().text();
	}
	
}
