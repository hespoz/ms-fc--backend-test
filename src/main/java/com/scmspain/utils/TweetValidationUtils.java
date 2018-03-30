package com.scmspain.utils;

/**
 * Created by hespoz on 3/30/18.
 */
public class TweetValidationUtils extends ValidateUtils {

    public boolean isValidTweet(String text) {
        String clearedStr = cleanTweet(text);
        return clearedStr.length() > 0 && clearedStr.length() <= 140;
    }

}
