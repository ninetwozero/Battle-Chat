package com.ninetwozero.battlechat.http;

import java.io.*;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LoginHtmlParserTest {
	
	private LoginHtmlParser mLoggedIn;
	private LoginHtmlParser mLoginError;
	
	public LoginHtmlParserTest() {
		try {
			mLoggedIn = new LoginHtmlParser(getFileAsString("/index.htm"));
			mLoginError = new LoginHtmlParser(getFileAsString("/index_error.htm"));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	private String getFileAsString(String path){
		//File file = new File(path);
		InputStream stream = LoginHtmlParserTest.class.getResourceAsStream(path);//new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	    StringBuilder output = new StringBuilder();
	    String line = null;

        try {
            while ((line = reader.readLine()) != null) {
              output.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
	}
	
	@Test
	public void testDetectmLoginError() { 
		Assert.assertFalse(mLoggedIn.hasErrorMessage());
		Assert.assertTrue(mLoginError.hasErrorMessage());
	}
	
	@Test
	public void testGetUserId() {
		Assert.assertEquals(mLoggedIn.getUserId(), Long.parseLong("2832658801548551060"));
	}
	
	@Test
	public void testGetUsername() {
		Assert.assertEquals(mLoggedIn.getUsername(), "ninetwozero");
	}
	
	@Test
	public void testGetChecksum() {
		Assert.assertThat(mLoggedIn.getChecksum(), CoreMatchers.not(""));
	}
	
	@Test
	public void testDetectmLoggedIn() {
		Assert.assertTrue(mLoggedIn.isLoggedIn());
		Assert.assertFalse(mLoginError.isLoggedIn());
	}
}
