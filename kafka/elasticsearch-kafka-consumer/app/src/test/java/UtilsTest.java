import com.github.uladzmi.eskc.Utils;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsTest {

    @Test
    public void testGetPropertiesFromResourcePathWithExistingFile() {
        Properties actual = Utils.getPropertiesFromResourcePath("test.properties");
        assertEquals("bar", actual.getProperty("foo"));
    }

    @Test
    public void testGetPropertiesFromResourcePathFileNotExists() {
        assertThrows(IllegalArgumentException.class, () -> Utils.getPropertiesFromResourcePath("no_such.properties"));
    }



}
