# twitter-kafka-producer

Simple Kafka producer making Twitter Filtered Stream available in Kafka.

## Prerequisites

- [Twitter Developer Account](https://developer.twitter.com)
- [Docker](https://www.docker.com/)

## Connect to Kafka running in other container

Assuming Kafka is running in a separate Docker container, e.g. [wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker), set the `BEARER_TOKEN` environment variable, build and run the container with
```bash
docker build -t tkp .
docker run --rm -it -e BEARER_TOKEN --net container:kafka -v $(pwd)/config:/opt/app/config tkp
```

By default `TwitterKafkaProducer` connects to the `localhost:9092` and sends tweets to the `tweets` topic as JSON strings.

## ToDo

- Unittests
- Documentation
- Proper configuration management (rules)
