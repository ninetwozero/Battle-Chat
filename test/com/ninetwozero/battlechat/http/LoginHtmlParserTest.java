package com.ninetwozero.battlechat.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class LoginHtmlParserTest {
	
	private LoginHtmlParser loggedIn;
	private LoginHtmlParser loginError;
	
	public LoginHtmlParserTest() {
		try {
			loggedIn = new LoginHtmlParser(getFileAsString("index.htm"));
			loginError = new LoginHtmlParser(getFileAsString("index_error.htm"));
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
	public void testDetectLoginError() { 
		assertFalse(loggedIn.hasErrorMessage());
		assertTrue(loginError.hasErrorMessage());
	}
	
	@Test
	public void testGetUserId() {
		assertEquals(loggedIn.getUserId(), Long.parseLong("2832658801548551060"));
	}
	
	@Test
	public void testGetUsername() {
		assertEquals(loggedIn.getUsername(), "ninetwozero");
	}
	
	@Test
	public void testGetChecksum() {
		assertThat(loggedIn.getChecksum(), not(""));
	}
	
	
}
