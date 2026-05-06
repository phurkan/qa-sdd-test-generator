package com.sdd.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Loads config.properties settings centrally.
 */
public class ConfigReader {
    private static Properties props = new Properties();
    private static final String CONFIG_PATH = "resources/config/config.properties";

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: config.properties not found. Using defaults.");
        }
    }

    public static String get(String key)      { return props.getProperty(key, ""); }
    public static String getAppBaseUrl()      { return get("app.base.url"); }
    public static String getApiBaseUrl()      { return get("api.base.url"); }
    public static String getSwaggerUrl()      { return get("swagger.url"); }
    public static String getBrowser()         { return get("browser"); }
    public static boolean isHeadless()        { return Boolean.parseBoolean(get("headless")); }
    public static String getJiraBaseUrl()     { return get("jira.base.url"); }
    public static String getJiraApiToken()    { return get("jira.api.token"); }
    public static String getJiraProjectKey()  { return get("jira.project.key"); }
}
