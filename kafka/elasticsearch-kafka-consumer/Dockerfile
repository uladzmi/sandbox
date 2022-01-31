FROM openjdk:8

RUN apt-get update && \
    apt-get install -y maven

COPY app /opt/app

RUN mvn -f /opt/app/pom.xml clean package

CMD java -cp /opt/app/target/elasticsearch-kafka-consumer-1.0.jar com.github.uladzmi.eskc.ESKafkaConsumer
