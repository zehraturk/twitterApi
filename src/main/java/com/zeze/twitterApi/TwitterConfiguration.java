package com.zeze.twitterApi;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfiguration {
    public static ConfigurationBuilder getConfig(){
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterConstants.OAuthConsumerKey)
                .setOAuthConsumerSecret(TwitterConstants.OAuthConsumerSecret)
                .setOAuthAccessToken(TwitterConstants.OAuthAccessToken)
                .setOAuthAccessTokenSecret(TwitterConstants.OAuthAccessTokenSecret);
        return configurationBuilder;
    }

    //cleaning tweet texts
    public static TwitterStatus cleanTweets(Status status){
        TwitterStatus twitterStatus = new TwitterStatus(status.getUser().getScreenName(),status.getCreatedAt().getTime(),
                                                            status.getId(),status.getUser().getFollowersCount(),status.getUser().getFriendsCount(),
                                                                    status.getUser().getDescription(),status.getRetweetCount(),status.getFavoriteCount(),
                                                                            status.getText(),status.getSource());
        // Clean up tweets

        String text = status.getText().toLowerCase().trim()
                // remove links
                .replaceAll("http.*?[\\S]+", "")
                // remove usernames
                .replaceAll("@[\\S]+", "")
                // replace hashtags by just words
                .replaceAll("#", "")
                // correct all multiple white spaces to a single white space
                .replaceAll("[\\s]+", " ")
                //remove uri
                .replaceAll("www[^\\s]+","")
                //remove rt
                .replaceAll("rt","")
                //remove trailing chars
                .replaceAll("[^a-zA-Z]+$", "")
                //remove punctuations
                .replaceAll("\\p{Punct}","");

        twitterStatus.setTwit_text(text);
        //twitterStatus.setSentimentType(analyzerService.analyse(text));
        return twitterStatus;
    }

}
