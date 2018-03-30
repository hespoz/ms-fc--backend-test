package com.scmspain.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by hespoz on 3/27/18.
 */
public class TweetValidationUtilsTest {

    private static final String LONG_TWEET = "Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 500 cuando https://www.validurl.com";
    private static final String CLEAN_LONG_TWEET = "Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 500 cuando";

    private static final String ORIGINAL_TWEET = "We are Schibsted Spain (look at our home page http://www.schibsted.es/ ), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!";
    private static final String CLEAN_TWEET = "We are Schibsted Spain (look at our home page  ), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!";

    private static final String LONG_TWEET_140 = "We are Schibsted Spain (look at our home page), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!gsdgsdfafwerqrqwerwer1";
    private static final String LONG_TWEET_141 = "We are Schibsted Spain (look at our home page), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!gsdgsdfafwerqrqwerwer11";


    private TweetValidationUtils tweetValidationUtils = new TweetValidationUtils();

    //Tests for cleanTweet
    @Test
    public void shouldGetExpectedCleanString() throws Exception {
        assertEquals(tweetValidationUtils.cleanTweet(ORIGINAL_TWEET), CLEAN_TWEET);
        assertEquals(tweetValidationUtils.cleanTweet(LONG_TWEET), CLEAN_LONG_TWEET);
    }

    @Test
    public void shouldGetEmptyCleanTweet_StringIsNull() throws Exception {
        assertEquals("", tweetValidationUtils.cleanTweet(null));
    }

    //Tests for isValidTweet
    @Test
    public void shouldTweetNotPassValidation_StringIsNull() throws Exception {
        assertFalse(tweetValidationUtils.isValidTweet(null));
    }

    @Test
    public void shouldTweetNotPassValidation_StringIsTooLong() throws Exception {
        assertFalse(tweetValidationUtils.isValidTweet(LONG_TWEET));
    }


    @Test
    public void shouldTweetPassValidation() throws Exception {
        assertTrue(tweetValidationUtils.isValidTweet(ORIGINAL_TWEET));
        assertTrue(tweetValidationUtils.isValidTweet(LONG_TWEET_140));
        assertFalse(tweetValidationUtils.isValidTweet(LONG_TWEET_141));
    }


    //Tests for nonEmpty
    @Test
    public void shouldReturnFalse_TextIsNull() throws Exception {
        assertFalse(tweetValidationUtils.nonEmpty(null));
    }

    @Test
    public void shouldReturnFalse_TextIsBlank() throws Exception {
        assertFalse(tweetValidationUtils.nonEmpty(""));
    }

    @Test
    public void shouldReturnTrue_TextIsValidString() throws Exception {
        assertTrue(tweetValidationUtils.nonEmpty("user"));
    }

    //Test validTweetId

    @Test
    public void shouldReturnTrue_IdIsValid() throws Exception {
        assertTrue(tweetValidationUtils.validTweetId("1"));
    }

    @Test
    public void shouldReturnTrue_IdIsNotValid() throws Exception {
        assertFalse(tweetValidationUtils.validTweetId("No a valid id"));
    }


}
