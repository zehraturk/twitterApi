package com.zeze.twitterApi;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class SparkApp {
    public static void main(String[] args) {

        //mongodb veri okuma
        SparkSession sparkSession_mongo = SparkSession.builder()
                .master("local")
                .appName("twitter-search-hashtags")
                .config("spark.mongodb.input.uri", "mongodb://localhost/twitterDB.search")
                .config("spark.mongodb.input.partitioner", "MongoPaginateBySizePartitioner")
                .config("spark.mongodb.input.partitionerOptions.partitionSizeMB", "64")
                .getOrCreate();

        sparkSession_mongo.sparkContext().setLogLevel("ERROR");

        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession_mongo.sparkContext());
        Dataset<Row> dataset = MongoSpark.load(javaSparkContext).toDF();

        StructType schema = new StructType()
                .add("tweet_id", DataTypes.StringType)
                .add("screen_name", DataTypes.StringType)
                .add("followers_count", DataTypes.IntegerType)
                .add("friends_count", DataTypes.IntegerType)
                .add("description", DataTypes.StringType)
                .add("created_at", DataTypes.StringType)
                .add("retweet_count", DataTypes.IntegerType)
                .add("favorite_count", DataTypes.IntegerType)
                .add("twit_text", DataTypes.StringType)
                .add("source", DataTypes.StringType);

        //kafkadan veri okuma
        /*Dataset<Row> dataset = sparkSession.read().format("kafka")
                .option("kafka.bootstrap.servers", "78.141.223.87:9092")
                .option("subscribe", "kirmizi-oda-tweetss ").load();

        //kafkadan okunan verinin value kısmı cast edildi.
        Dataset<Row> castedDataset = dataset.selectExpr("CAST(value AS STRING)");
        Dataset<Row> valueDataset = castedDataset.select(functions.from_json(castedDataset.col("value"), schema).as("data")).select("data.*");*/

        //toplam etkileşim sayısı için yeni bir kolon eklendi
        Dataset<Row> twitterDataSet = dataset.withColumn("interaction_count", dataset.col("retweet_count").$plus(dataset.col("favorite_count")));

        //tarihe göre gruplandı.
        Dataset<Row> created_at_Groups = twitterDataSet.groupBy("created_at").count();
        System.out.println("created at groups");

        //kulllanıcıların etkileşim sayıları distinct kullanıcılar.
        Dataset<Row> user_Interactions = twitterDataSet.select("screen_name", "interaction_count").dropDuplicates();

        //kullancılar twit sayılarına göre gruplandı.
        Dataset<Row> user_Groups = twitterDataSet.groupBy("screen_name").count().sort(functions.desc("count")).withColumnRenamed("screen_name","screenName");

        //kullanıcıların twit sayıları ve etkileşim sayıları
        Dataset<Row> user_Counts=user_Groups.join(user_Interactions,user_Groups.col("screenName").equalTo(user_Interactions.col("screen_name")))
                .groupBy("screenName","count").sum("interaction_count")
                .drop("screen_name");


        //kullancı bilgileri etkileşim sayısına göre listelendi.
        Dataset<Row> common_Users=user_Counts.join(twitterDataSet,user_Counts.col("screenName").equalTo(twitterDataSet.col("screen_name")))
                .select("screen_name","description","sum(interaction_count)","count","followers_count","friends_count").dropDuplicates("screen_name");
        System.out.println("common users");
        common_Users.sort(functions.desc("sum(interaction_count)")).show();

        //twit textleri bir dataset yapıldı.
        Dataset<Row> twit_textsDF = dataset.select("twit_text");

        //en çok geçen kelimeler
        Dataset<Row> word_countsDF = twit_textsDF.withColumn("word", functions.explode(functions.split(twit_textsDF.col("twit_text")," ")))
                .groupBy("word")
                .count()
                .sort(functions.desc("count"));



    }

}
