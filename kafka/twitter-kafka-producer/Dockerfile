FROM openjdk:8

RUN apt-get update && \
    apt-get install -y maven

COPY app /opt/app

RUN mvn -f /opt/app/pom.xml clean package

CMD java -cp /opt/app/target/app-1.0.jar com.github.uladzmi.tkp.TwitterKafkaProducer
