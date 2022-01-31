package com.github.uladzmi.eskc;

public class DefaultConfig {

    /** Kafka config. */
    public final static String DEFAULT_TOPIC = "tweets";
    public final static Integer DEFAULT_POLL_TIMEOUT = 100;


    /** Elasticsearch config. */
    public final static String DEFAULT_ELASTICSEARCH_SERVER = "localhost:9200";
    public final static String DEFAULT_ELASTICSEARCH_INDEX = "twitter";
    public final static String DEFAULT_ELASTIC_SEARCH_INDEX_TYPE = "tweets";

}
