package com.scmspain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)

/*
 * I add this to restart the H2 db each time a test is executed. Other approach will be to use @BeforeAll.
 * But for this we need to make the setUp method static, and in this case that will affect the autowired of WebApplicationContext.
 * This is a little bit slow, but works fine!
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {

    private static final String LONG_TWEET_WITH_LINK = "We are Schibsted Spain (look at our home page http://www.schibsted.es/ ), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!";
    private static final String LONG_TWEET_WITH_NO_LINK = "We are Schibsted Spain (look at our home page XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX ), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!";

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(this.context).build();
    }


    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        mockMvc.perform(newTweet("Prospect", "Breaking the law"))
                .andExpect(status().is(201));
    }

    @Test
    public void shouldReturn200WhenInsertingAnLongTweetWithUrl() throws Exception {
        mockMvc.perform(newTweet("Schibsted Spain", LONG_TWEET_WITH_LINK))
                .andExpect(status().is(201));
    }


    @Test
    public void shouldReturn400WhenSentNullPublisher() throws Exception {
        mockMvc.perform(newTweet(null, LONG_TWEET_WITH_LINK))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturn400WhenSentBlankPublisher() throws Exception {
        mockMvc.perform(newTweet("", LONG_TWEET_WITH_LINK))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturn400WhenInsertingATweetWithMoreThan140Characters() throws Exception {
        mockMvc.perform(newTweet("Schibsted Spain", LONG_TWEET_WITH_NO_LINK))
                .andExpect(status().is(400));
    }


    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {

        mockMvc.perform(newTweet("Yo", "How are you, Rap?"))
                .andExpect(status().is(201));


        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(1);
    }

    @Test
    public void shouldFailDiscardedTweetNotFound() throws Exception {
        mockMvc.perform(discardTweet("333342"))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldFailDiscardedTweetNotValidTweetId() throws Exception {
        mockMvc.perform(discardTweet("NotValidId"))
                .andExpect(status().is(400));
    }


    @Test
    public void shouldReturnAllDiscardedTweets() throws Exception {

        mockMvc.perform(newTweet("Yo", "How are you, Ayn Rand?"))
                .andExpect(status().is(201));

        mockMvc.perform(newTweet("Yo", "How are you, Alan Pike?"))
                .andExpect(status().is(201));

        mockMvc.perform(newTweet("Yo", "How are you, Friedman?"))
                .andExpect(status().is(201));


        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        List<Tweet> tweetList = new ObjectMapper().readValue(content, List.class);
        assertThat(tweetList.size()).isEqualTo(3);


        //Discard one tweet.
        mockMvc.perform(discardTweet("3"))
                .andExpect(status().is(201));

        //Check if the list of tweets now has only 2 tweets.
        MvcResult getResultTweetsAfterDiscard = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        assertThat(new ObjectMapper().readValue(getResultTweetsAfterDiscard.getResponse().getContentAsString(), List.class).size()).isEqualTo(2);


        //Check if discarded tweets list has only 1 discarded tweet.
        MvcResult getResultDiscardedTweets = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200))
                .andReturn();

        assertThat(new ObjectMapper().readValue(getResultDiscardedTweets.getResponse().getContentAsString(), List.class).size()).isEqualTo(1);


    }

    private MockHttpServletRequestBuilder discardTweet(String tweetId) {
        return post("/discarded")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"tweet\": \"%s\"}", tweetId));
    }

    private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        final String jsonString =
                publisher != null ?
                        format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet)
                        :
                        format("{\"publisher\": null, \"tweet\": \"%s\"}", tweet);

        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonString);
    }

}
