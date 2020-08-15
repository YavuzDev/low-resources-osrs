package com.osrs;

import com.osrs.api.Client;
import com.osrs.frame.ClientFrame;
import com.osrs.frame.OsrsAppletStub;
import com.osrs.util.OsrsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class RendererStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(RendererStart.class);

    private static final String CONFIG_URL = "http://oldschool83.runescape.com/jav_config.ws";

    private static final String MAIN_CLASS_NAME = "client";

    private static final String TITLE = "Low Resources Osrs Client";

    public static final Path RENDER_DIRECTORY = HooksFinder.RESOURCES_DIRECTORY.resolve("render");

    public static final Path CONFIG_FILE = RENDER_DIRECTORY.resolve("jav_config.ws");

    public static final boolean SAVE_CONFIG_FILE = true;

    private static final boolean INJECT_HOOKS = false;

    public static final boolean DOWNLOAD_CONFIGS = false;

    public static void main(String[] args) throws Exception {
        if (INJECT_HOOKS) {
            LOGGER.info("Injecting hooks");
            HooksInjector.main(args);
        }

        if (!Files.exists(HooksInjector.INJECTED_JAR)) {
            throw new FileNotFoundException(HooksInjector.INJECTED_JAR + " not found set INJECT_HOOKS to true");
        }

        OsrsConfig config;
        if (DOWNLOAD_CONFIGS) {
            config = OsrsConfig.load(CONFIG_URL);
        } else {
            config = OsrsConfig.load(CONFIG_FILE.toUri().toURL());
        }

        LOGGER.info("Loading {} class to render game", MAIN_CLASS_NAME);
        var classLoader = new URLClassLoader(new URL[]{HooksInjector.INJECTED_JAR.toUri().toURL()});
        var clientClass = classLoader.loadClass(MAIN_CLASS_NAME);
        var client = (Client) clientClass.newInstance();

        LOGGER.info("Initializing frame with title {}", TITLE);
        var frame = new ClientFrame(800, 600, TITLE);
        client.setStub(new OsrsAppletStub(config));
        frame.renderGame(client);
        LOGGER.info("Frame started");
    }
}
