package com.github.uladzmi.tkp;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.github.uladzmi.tkp.EnvironmentConfig.*;

public class TwitterKafkaProducer {

    /**  Logger. */
    final static Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());

    public static void main(String[] args) {

        final String bearerToken = System.getenv(BEARER_TOKEN_ENV);

        if (null != bearerToken) {

            TwitterStreamClient twitterClient =
                    new TwitterStreamClient(TwitterCredentials.builder().bearerToken(bearerToken).build());

            twitterClient.updateRules();

            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(getKafkaProducerProperties());
            String topicName = System.getenv().getOrDefault(TOPIC_NAME_ENV, "tweets");

            twitterClient.startFilteredStream(new TwitterStreamKafkaHandler(kafkaProducer, topicName));

        } else {
            logger.error("Please make sure to set the {} environment variable", BEARER_TOKEN_ENV);
        }

    }

    /** Get Kafka producer properties from resources and environment. */
    public static Properties getKafkaProducerProperties() {

        String producerProperties = "producer.properties";
        Properties properties = Utils.getPropertiesFromResourcePath(producerProperties);

        final String bootstrapServer = System.getenv()
                .getOrDefault(BOOTSTRAP_SERVERS_ENV, properties.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return properties;
    }


}
