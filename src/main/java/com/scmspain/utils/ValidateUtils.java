package com.scmspain.utils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by hespoz on 3/27/18.
 */
public class ValidateUtils {

    private final static String URL_REGEX ="((http:\\/\\/|https:\\/\\/)([^\\s]+)?)";

    public static String cleanTweet(String text) {
        return Pattern.compile(URL_REGEX)
                .matcher(Optional.ofNullable(text).orElse(""))
                .replaceAll("").trim();
    }

    public static boolean isValidTweet(String text) {
        String clearedStr = cleanTweet(text);
        return clearedStr.length() > 0 && clearedStr.length() <= 140;
    }

    public static boolean nonEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public static boolean validTweetId(String id) {
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
