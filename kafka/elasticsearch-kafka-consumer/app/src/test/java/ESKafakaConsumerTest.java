import com.github.uladzmi.eskc.ESKafkaConsumer;
import com.github.uladzmi.eskc.EnvironmentConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ESKafakaConsumerTest {

        @Test
        public void testGetKafkaConsumerPropertiesEnvironmentOverwrite() throws Exception {

            String bootstrapServers = "localhost:1234";

            Properties properties =
                    withEnvironmentVariable(EnvironmentConfig.BOOTSTRAP_SERVERS_ENV, bootstrapServers)
                            .execute(ESKafkaConsumer::getKafkaConsumerProperties);
            assertEquals(bootstrapServers, properties.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        }

    @Test
    public void testGetKafkaConsumerPropertiesNoEnvironment() throws Exception {

        assertEquals("localhost:9092",
                ESKafkaConsumer.getKafkaConsumerProperties().getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

}
