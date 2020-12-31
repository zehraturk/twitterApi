package com.zeze.twitterApi;
import twitter4j.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearch {
    public static ArrayList<Status> getAdvancedSearch(Twitter twitter,Query query){
        int numberOfTweets = 500;
        long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size () < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100)
                query.setCount(100);
            else
                query.setCount(numberOfTweets - tweets.size());
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println(tweets.size()+"toplandı");
                for (Status t: tweets)
                    if(t.getId() < lastID) lastID = t.getId();
            }
            catch (TwitterException te) {
                System.out.println("couldnt connect to twitter");
                break;
            };
            query.setMaxId(lastID-1);
        }
        return (ArrayList<Status>) tweets;

    }
}
