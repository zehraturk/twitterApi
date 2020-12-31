package com.zeze.twitterApi;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;

//retrieve tweets by search query and save mongodb
public class MongoDataSink {
    public static void main(String[] args) {
        //mongo connect
        MongoClient mongoClient = new MongoClient(MongoConstants.MONGO_HOST,MongoConstants.MONGO_PORT);
        MongoDatabase twitterDB = mongoClient.getDatabase("twitterDB");
        MongoCollection<Document> searchCollection = twitterDB.getCollection("search");

        //twitter
        ConfigurationBuilder configurationBuilder = TwitterConfiguration.getConfig();
        TwitterFactory tf=new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();

        Query query=new Query();
        query.setQuery("#KırmızıOda");
        //query.setLang("en");
        query.setSince("2020-12-22");

        ArrayList<Status> tweetList = AdvancedSearch.getAdvancedSearch(twitter,query);

        System.out.println("for a girdi");
        for(Status status_:tweetList){
            TwitterStatus status = TwitterConfiguration.cleanTweets(status_);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("tweet_id",status.getTweet_id());
            jsonObject.put("screen_name",status.getScreen_name());
            jsonObject.put("followers_count",status.followers_count);
            jsonObject.put("friends_count",status.friends_count);
            jsonObject.put("description",status.getDescription());
            long time = status.getCreated_at();
            String str_time= String.valueOf(time);
            jsonObject.put("created_at",DateParser.parseDate(str_time));
            jsonObject.put("retweet_count",status.getRetweet_count());
            jsonObject.put("favorite_count",status.getFavorite_count());
            jsonObject.put("twit_text",status.getTwit_text());
            jsonObject.put("source",status.getSource());
            String tweet=jsonObject.toString();
            searchCollection.insertOne(Document.parse(tweet));
        }
    }

}
