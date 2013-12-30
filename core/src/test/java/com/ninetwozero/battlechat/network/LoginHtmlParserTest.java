package com.ninetwozero.battlechat.network;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginHtmlParserTest {

    private LoginHtmlParser loggedInParser;
    private LoginHtmlParser loginErrorParser;

    public LoginHtmlParserTest() {
        try {
            loggedInParser = new LoginHtmlParser(getFileAsString("/htm/index.htm"));
            loginErrorParser = new LoginHtmlParser(getFileAsString("/htm/index_error.htm"));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private String getFileAsString(String path) {
        InputStream stream = LoginHtmlParserTest.class.getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder output = new StringBuilder();
        String line;

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
        Assert.assertFalse(loggedInParser.hasErrorMessage());
        Assert.assertTrue(loginErrorParser.hasErrorMessage());
    }

    @Test
    public void testGetUserId() {
        Assert.assertEquals(loggedInParser.getUserId(), "2832658801548551060");
    }

    @Test
    public void testGetUsername() {
        Assert.assertEquals(loggedInParser.getUsername(), "ninetwozero");
    }

    @Test
    public void testGetChecksum() {
        Assert.assertThat(loggedInParser.getChecksum(), CoreMatchers.not(""));
    }

    @Test
    public void testDetectLoggedIn() {
        Assert.assertTrue(loggedInParser.isLoggedIn());
        Assert.assertFalse(loginErrorParser.isLoggedIn());
    }

    @Test
    public void testGetGravatar(){
        Assert.assertEquals(loggedInParser.getGravatarUrl(), "http://www.gravatar.com/avatar/1241459af7d1ba348ec8b258240ea145.png?s=320");
    }
}
