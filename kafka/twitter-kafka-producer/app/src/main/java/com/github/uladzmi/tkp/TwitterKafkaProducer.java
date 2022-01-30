package com.github.uladzmi.tkp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.uladzmi.tkp.dto.FilteredStreamRule;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.stream.StreamRules;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class TwitterKafkaProducer {

    final static String BEARER_TOKEN_ENV = "BEARER_TOKEN";
    final static String BOOTSTRAP_SERVERS_ENV = "BOOTSTRAP_SERVERS";
    final static String TOPIC_NAME_ENV = "TOPIC";

    final static Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static KafkaProducer<String, String> getKafkaProducer() {

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("/opt/app/src/main/resources/producer.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Failed to load producer.properties: " + ex.getMessage());
        }

        final String bootstrapServer = System.getenv()
                .getOrDefault(BOOTSTRAP_SERVERS_ENV, properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        logger.info("Connecting to: " + properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

        return new KafkaProducer<>(properties);
    }

    public static void updateRules(TwitterClient twitterClient) {
        // delete existing rules
        List<StreamRules.StreamRule> existingRules = twitterClient.retrieveFilteredStreamRules();
        if (null != existingRules) {
            logger.info("Removing existing rules: " + existingRules);
            existingRules.forEach(rule -> twitterClient.deleteFilteredStreamRuleId(rule.getId()));
        }
        // read rules
        try {
            List<FilteredStreamRule> rules =
                    objectMapper.readValue(
                            new File("/opt/app/config/rules.json"),
                            new TypeReference<List<FilteredStreamRule>>(){}
                    );

            if (rules != null) {
                logger.info("Adding following rules:" + rules);
                rules.forEach(rule -> twitterClient.addFilteredStreamRule(rule.getValue(), rule.getTag()));
            }
        } catch (JsonParseException e) {
            logger.warn("Error parsing rules: " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Error reading rules config file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        final String bearerToken = System.getenv(BEARER_TOKEN_ENV);

        if (null != bearerToken) {

            TwitterClient twitterClient = new TwitterClient(
                    TwitterCredentials.builder()
                            .bearerToken(bearerToken)
                            .build()
            );

            updateRules(twitterClient);

            KafkaProducer<String, String> kafkaProducer = getKafkaProducer();
            String topicName = System.getenv().getOrDefault(TOPIC_NAME_ENV, "tweets");

            logger.info("Producing messages to: " + topicName);

            twitterClient.startFilteredStream(new TKPListener(kafkaProducer, topicName));

        } else {
            logger.error("Please make sure to set the {} environment variable", BEARER_TOKEN_ENV);
        }

    }

}
