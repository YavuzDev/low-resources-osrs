package com.bot.util;

import com.bot.RendererStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class OsrsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsrsConfig.class);

    private final Map<String, String> appletProperties;

    private final Map<String, String> classLoaderProperties;

    public static OsrsConfig load(String url) throws IOException {
        return load(new URL(url));
    }

    public static OsrsConfig load(URL url) throws IOException {
        LOGGER.info("Loading configs from {}", url);
        return load(new InputStreamReader(url.openStream()));
    }

    public static OsrsConfig load(Reader reader) throws IOException {
        var appletProperties = new HashMap<String, String>();
        var classLoaderProperties = new HashMap<String, String>();

        if (RendererStart.SAVE_CONFIG_FILE) {
            LOGGER.info("Saving config file at {}", RendererStart.CONFIG_FILE);
            if (Files.exists(RendererStart.CONFIG_FILE)) {
                Files.delete(RendererStart.CONFIG_FILE);
            }
            Files.createFile(RendererStart.CONFIG_FILE);
        }

        try (var bufferedReader = new BufferedReader(reader)) {
            bufferedReader.lines().forEach(line -> {
                if (RendererStart.SAVE_CONFIG_FILE) {
                    try {
                        Files.write(RendererStart.CONFIG_FILE, (line + "\n").getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (line.startsWith("param=")) {
                    var pair = line.substring(6);
                    var split = pair.split("=", 2);
                    if (split.length == 1) {
                        appletProperties.put(split[0], "");
                    } else {
                        appletProperties.put(split[0], split[1]);
                    }
                } else if (!line.startsWith("msg=")) {
                    var split = line.split("=", 2);
                    classLoaderProperties.put(split[0], split[1]);
                }
            });
        }
        return new OsrsConfig(appletProperties, classLoaderProperties);
    }

    public OsrsConfig(Map<String, String> appletProperties, Map<String, String> classLoaderProperties) {
        this.appletProperties = appletProperties;
        this.classLoaderProperties = classLoaderProperties;
    }

    public String getAppletProperty(String key) {
        return appletProperties.get(key);
    }

    public String getCodebase() {
        return classLoaderProperties.get("codebase");
    }

    public Map<String, String> getAppletProperties() {
        return appletProperties;
    }

    public Map<String, String> getClassLoaderProperties() {
        return classLoaderProperties;
    }

    @Override
    public String toString() {
        return "OsrsConfig{" +
                "appletProperties=" + appletProperties +
                ", classLoaderProperties=" + classLoaderProperties +
                '}';
    }
}
