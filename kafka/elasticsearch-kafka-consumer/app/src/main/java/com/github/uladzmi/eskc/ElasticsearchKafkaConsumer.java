package com.github.uladzmi.eskc;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static com.github.uladzmi.eskc.DefaultConfig.*;
import static com.github.uladzmi.eskc.EnvironmentConfig.*;


public class ElasticsearchKafkaConsumer {

    /**  Logger. */
    public static final Logger logger = LoggerFactory.getLogger(ElasticsearchKafkaConsumer.class);

    /** App entry point. */
    public static void main(String[] args) throws IOException {

        // Create Kafka consumer and subscribe to topic
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getKafkaConsumerProperties());
        String topicName = System.getenv().getOrDefault(TOPIC_NAME_ENV, DEFAULT_TOPIC);

        kafkaConsumer.subscribe(Collections.singletonList(topicName));

        // Create Elasticsearch client
        String elasticsearchServer = System.getenv().getOrDefault(ELASTICSEARCH_HOST_ENV, DEFAULT_ELASTICSEARCH_SERVER);
        String elasticsearchIndex = System.getenv().getOrDefault(ELASTICSEARCH_INDEX_ENV, DEFAULT_ELASTICSEARCH_INDEX);
        String elasticsearchIndexType = System.getenv().getOrDefault(
                ELASTICSEARCH_INDEX_TYPE_ENV, DEFAULT_ELASTIC_SEARCH_INDEX_TYPE);

        RestHighLevelClient elasticsearchClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(elasticsearchServer)));

        // Main loop
        while (true) {
            // Polling messages from Kafka
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(DEFAULT_POLL_TIMEOUT));

            int recordCount = records.count();
            logger.info("Received " + recordCount + " records ...");

            if (recordCount == 0) {
                continue;
            }

            BulkRequest bulkRequest = createElasticsearchBulkRequest(
                    records, elasticsearchIndex, elasticsearchIndexType);

            // Send batch to Elasticsearch and commit the offsets
            elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            logger.info("Committing offsets...");
            kafkaConsumer.commitSync();

        }

//         close the client gracefully
//         elasticsearchClient.close();

    }

    /** Send Elasticsearch bulk request*/
    public static BulkRequest createElasticsearchBulkRequest(ConsumerRecords<String, String> records,
                                                    String elasticsearchIndex, String elasticsearchIndexType ) {

        // Initialize Elasticsearch bulk request
        BulkRequest bulkRequest = new BulkRequest();

        for (ConsumerRecord<String, String> record: records) {

            // Use unique IndexRequest id to make consumer idempotent.
            // Better would be to use tweet id, but the combination below is sufficient as well.
            IndexRequest request = new IndexRequest(elasticsearchIndex, elasticsearchIndexType)
                    .source(record.value(), XContentType.JSON)
                    .id(record.topic() + "_" + record.partition() + "_" + record.offset());

            bulkRequest.add(request);
        }

        return bulkRequest;

    }

    /** Get Kafka consumer properties from resources and environment. */
    public static Properties getKafkaConsumerProperties() {

        String consumerProperties = "consumer.properties";
        Properties properties = Utils.getPropertiesFromResourcePath(consumerProperties);

        final String bootstrapServer = System.getenv()
                .getOrDefault(BOOTSTRAP_SERVERS_ENV, properties.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return properties;
    }
}
