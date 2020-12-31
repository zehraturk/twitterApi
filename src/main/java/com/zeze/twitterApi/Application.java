package com.zeze.twitterApi;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;
import java.util.Properties;

public class Application {
    public static void main(String[] args) throws TwitterException {
        //mongo
        MongoClient mongoClient = new MongoClient(MongoConstants.MONGO_HOST,MongoConstants.MONGO_PORT);
        MongoDatabase twitterDB = mongoClient.getDatabase("twitterDB");
        MongoCollection<Document> searchCollection = twitterDB.getCollection("KirmiziOdaTweets");

        //kafka configs
        final String KAFKA_HOST="machine IP:9092";
        final String KAFKA_TOPIC="kirmizi-oda-tweetss";
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,KAFKA_HOST);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,new StringSerializer().getClass().getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,new StringSerializer().getClass().getName());
        KafkaProducer<String, String> kafkaProducer=new KafkaProducer<String, String>(properties);

        ConfigurationBuilder config = TwitterConfiguration.getConfig();
        TwitterFactory tf=new TwitterFactory(config.build());
        Twitter twitter = tf.getInstance();

        Query query=new Query();
        query.setQuery("#KırmızıOda");
        //query.setLang("en");
        query.setSince("2020-10-1");
        query.setUntil("2020-10-10");
        ArrayList<Status> tweetList = AdvancedSearch.getAdvancedSearch(twitter,query);

        //writing mongo and kafka
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
            ProducerRecord<String, String> producerRecord=new ProducerRecord<String, String>(KAFKA_TOPIC,tweet);
            kafkaProducer.send(producerRecord);
            searchCollection.insertOne(Document.parse(tweet));

        }
    }
}
