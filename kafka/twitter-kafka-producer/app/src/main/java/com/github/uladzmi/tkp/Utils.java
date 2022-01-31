package com.github.uladzmi.tkp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    /**
     * Get java.util.Properties object from resource path.
     * @param path resource path within resources folder
     * */
    public static Properties getPropertiesFromResourcePath(String path) {

        Properties properties = new Properties();
        // The class loader that loaded the class
        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource file not found! " + path);
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IllegalArgumentException("Resource file not found! " + path);
            }
        }
        return properties;
    }

}
