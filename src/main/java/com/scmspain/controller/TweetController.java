package com.scmspain.controller;

import com.scmspain.controller.command.DiscardTweetCommand;
import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class TweetController {
    private TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweet")
    public List<Tweet> listAllTweets() {
        return this.tweetService.listAllTweets();
    }

    @GetMapping("/discarded")
    public List<Tweet> listDiscardedTweets() {
        return this.tweetService.listDiscardedTweets();
    }


    @PostMapping("/tweet")
    @ResponseStatus(CREATED)
    public Tweet publishTweet(@RequestBody PublishTweetCommand publishTweetCommand) {
        return this.tweetService.publishTweet(publishTweetCommand.getPublisher(), publishTweetCommand.getTweet());
    }

    //I would name this /tweet but with PUT, because it is basically an update.
    @PostMapping("/discarded")
    @ResponseStatus(CREATED)
    public Tweet discardTweet(@RequestBody DiscardTweetCommand discardTweetCommand) {
        return this.tweetService.discardTweet(discardTweetCommand.getTweet());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Object invalidArgumentException(IllegalArgumentException ex) {
        return new Object() {
            public String message = ex.getMessage();
            public String exceptionClass = ex.getClass().getSimpleName();
        };
    }
}
