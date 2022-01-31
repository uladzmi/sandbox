package com.github.uladzmi.tkp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.uladzmi.tkp.dto.FilteredStreamRule;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.stream.StreamRules;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TwitterStreamClient extends TwitterClient {

    /**  Logger. */
    final static Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());

    /** ObjectMapper. */
    private static final ObjectMapper objectMapper = new ObjectMapper();


    /** TwitterStreamClient constructor. */
    public TwitterStreamClient(TwitterCredentials credentials) {
        super(credentials);
    }

    /** Set FilteredStream rules. */
    public void updateRules() {
        // delete existing rules
        List<StreamRules.StreamRule> existingRules = retrieveFilteredStreamRules();
        if (null != existingRules) {
            logger.info("Removing existing rules: " + existingRules);
            existingRules.forEach(rule -> deleteFilteredStreamRuleId(rule.getId()));
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
                rules.forEach(rule -> addFilteredStreamRule(rule.getValue(), rule.getTag()));
            }
        } catch (JsonParseException e) {
            logger.warn("Error parsing rules: " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Error reading rules config file: " + e.getMessage());
        }
    }

}
