package com.scmspain.utils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by hespoz on 3/27/18.
 */
public abstract class ValidateUtils {

    private final String URL_REGEX ="((http:\\/\\/|https:\\/\\/)([^\\s]+)?)";

    public String cleanTweet(String text) {
        return Pattern.compile(URL_REGEX)
                .matcher(Optional.ofNullable(text).orElse(""))
                .replaceAll("").trim();
    }


    public boolean nonEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public boolean validTweetId(String id) {
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
