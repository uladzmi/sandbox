package com.github.uladzmi.tkp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.redouane59.twitter.IAPIEventListener;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TKPListener implements IAPIEventListener {

    final Logger logger = LoggerFactory.getLogger(TKPListener.class.getName());

    public final KafkaProducer<String, String> kafkaProducer;
    public final String topicName;

    /** Twitter Kafka Producer listener. */
    public TKPListener(KafkaProducer<String, String> kafkaProducer, String topicName) {
        this.kafkaProducer = kafkaProducer;
        this.topicName = topicName;
    }

    @Override
    public void onStreamError(int i, String s) {
        logger.error("StreamError " + i + ": " + s);
    }

    /** Forward tweets to Kafka. */
    public void onTweetStreamed(Tweet tweet) {
        JsonNode tweetJson = TwitterClient.OBJECT_MAPPER.convertValue(tweet, new TypeReference<JsonNode>() {});
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, tweetJson.toString());
        kafkaProducer.send(record);
        logger.debug(record.toString());
    }

    /** Log UnknownDataStreamed error. */
    public void onUnknownDataStreamed(String s) {
        logger.error("UnknownDataStreamed: " + s);
    }

    /** Close Kafka producer. */
    public void onStreamEnded(Exception e) {
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
