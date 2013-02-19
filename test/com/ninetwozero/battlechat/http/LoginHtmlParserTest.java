package com.ninetwozero.battlechat.http;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;


public class LoginHtmlParserTest {
	
	private LoginHtmlParser mLoggedIn;
	private LoginHtmlParser mLoginError;
	
	public LoginHtmlParserTest() {
		try {
			mLoggedIn = new LoginHtmlParser(getFileAsString("index.htm"));
			mLoginError = new LoginHtmlParser(getFileAsString("index_error.htm"));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	private String getFileAsString(String path) throws Exception {
		File file = new File(path);
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	    StringBuilder output = new StringBuilder();
	    String line = null;
	    
	    while ((line = reader.readLine()) != null) {
	      output.append(line).append("\n");
	    }
	    
	    stream.close();
	    return output.toString();
	}
	
	@Test
	public void testDetectmLoginError() { 
		assertFalse(mLoggedIn.hasErrorMessage());
		assertTrue(mLoginError.hasErrorMessage());
	}
	
	@Test
	public void testGetUserId() {
		assertEquals(mLoggedIn.getUserId(), Long.parseLong("2832658801548551060"));
	}
	
	@Test
	public void testGetUsername() {
		assertEquals(mLoggedIn.getUsername(), "ninetwozero");
	}
	
	@Test
	public void testGetChecksum() {
		assertThat(mLoggedIn.getChecksum(), not(""));
	}
	
	@Test
	public void testDetectmLoggedIn() {
		assertTrue(mLoggedIn.ismLoggedIn());
		assertFalse(mLoginError.ismLoggedIn());
	}
	
}
