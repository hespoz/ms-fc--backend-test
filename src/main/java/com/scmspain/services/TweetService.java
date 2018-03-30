package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.utils.TweetValidationUtils;
import com.scmspain.utils.ValidateUtils;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TweetService {
    private EntityManager entityManager;
    private MetricWriter metricWriter;
    private TweetValidationUtils tweetValidationUtils;


    public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
        this.entityManager = entityManager;
        this.metricWriter = metricWriter;
        tweetValidationUtils = new TweetValidationUtils();
    }

    /**
      Push tweet to repository - I decide to return something because before the response was empty.
      Parameter - publisher - creator of the Tweet
      Parameter - text - Content of the Tweet
      Result - publised Tweet
    */
    public Tweet publishTweet(String publisher, String text) {

        //We can work more in the validation methods a DTOs with a list of errors.
        if(!tweetValidationUtils.nonEmpty(publisher)) {
            throw new IllegalArgumentException("Publisher can not be null");
        }

        if(!tweetValidationUtils.isValidTweet(text)) {
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }

        Tweet tweet = new Tweet();
        tweet.setTweet(text);
        tweet.setPublisher(publisher);

        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
        this.entityManager.persist(tweet);

        return tweet;

    }

    /**
     Discard tweet - I decide to return something because before the response was empty.
     Parameter - id - id of the Tweet to discard
     Result - discarded Tweet
     */
    public Tweet discardTweet(String tweetId) {

        if (!tweetValidationUtils.isValidTweet(tweetId)) {
            throw new IllegalArgumentException("Wrong id, should be a number");
        }

        Tweet tweet = Optional.ofNullable(this.entityManager.find(Tweet.class, Long.parseLong(tweetId)))
                .orElseThrow(() -> new IllegalArgumentException(String.format("There is no tweet with id %s ", tweetId)));

        tweet.setPre2015MigrationStatus(99l);
        this.entityManager.persist(tweet);

        return tweet;
    }

    /**
      Recover tweet from repository
      Parameter - id - id of the Tweet to retrieve
      Result - retrieved Tweet
    */
    public List<Tweet> listAllTweets() {
        List<Tweet> result = new ArrayList<Tweet>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));

        //I had to refactor this, there is no reason to go grab first the list of the Ids an then query the database n times again. This is performance killers!
        return this.entityManager.createQuery("SELECT tweet FROM Tweet tweet WHERE pre2015MigrationStatus<>99 ORDER BY id DESC", Tweet.class).getResultList();
    }

    public List<Tweet> listDiscardedTweets() {
        return this.entityManager.createQuery("SELECT tweet FROM Tweet tweet WHERE pre2015MigrationStatus=99 ORDER BY id DESC", Tweet.class).getResultList();
    }

}
