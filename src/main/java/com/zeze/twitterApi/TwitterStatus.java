package com.zeze.twitterApi;

public class TwitterStatus {
    String screen_name;
    long created_at;
    long tweet_id;
    int followers_count;
    int friends_count;
    String description;
    int retweet_count;
    int favorite_count;
    String twit_text;
    String source;

    public TwitterStatus(String screen_name, long created_at, long tweet_id, int followers_count, int friends_count, String description, int retweet_count, int favorite_count, String twit_text, String source) {
        this.screen_name = screen_name;
        this.created_at = created_at;
        this.tweet_id = tweet_id;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.description = description;
        this.retweet_count = retweet_count;
        this.favorite_count = favorite_count;
        this.twit_text = twit_text;
        this.source = source;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public String getTwit_text() {
        return twit_text;
    }

    public void setTwit_text(String twit_text) {
        this.twit_text = twit_text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
