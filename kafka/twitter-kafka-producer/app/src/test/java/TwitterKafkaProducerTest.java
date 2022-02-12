import com.github.uladzmi.tkp.EnvironmentConfig;
import com.github.uladzmi.tkp.TwitterKafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwitterKafkaProducerTest {

    @Test
    public void testGetKafkaConsumerPropertiesEnvironmentOverwrite() throws Exception {

        String bootstrapServers = "localhost:1234";

        Properties properties =
                withEnvironmentVariable(EnvironmentConfig.BOOTSTRAP_SERVERS_ENV, bootstrapServers)
                        .execute(TwitterKafkaProducer::getKafkaProducerProperties);
        assertEquals(bootstrapServers, properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    public void testGetKafkaConsumerPropertiesNoEnvironment() {

        assertEquals("localhost:9092",
                TwitterKafkaProducer.getKafkaProducerProperties().getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

}