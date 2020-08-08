package com.bot;

import com.google.gson.Gson;
import com.bot.hook.Hooks;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HooksInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HooksInjector.class);

    private static final String JAR_URL = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";

    public static final Path INJECTOR_DIRECTORY = HooksFinder.RESOURCES_DIRECTORY.resolve("injector");

    public static final Path JAR_FILE = INJECTOR_DIRECTORY.resolve("gamepack.jar");

    public static final Path INJECTED_JAR = INJECTOR_DIRECTORY.resolve("gamepack-injected.jar");

    private static final boolean FIND_HOOKS = false;

    private static final boolean DOWNLOAD_JAR = false;

    public static void main(String[] args) throws Exception {
        if (FIND_HOOKS) {
            LOGGER.info("Finding hooks");
            HooksFinder.main(args);
        }

        if (DOWNLOAD_JAR) {
            LOGGER.info("Downloading jar from {}", JAR_URL);
            FileUtils.copyURLToFile(new URL(JAR_URL), JAR_FILE.toFile());
        }

        if (!Files.exists(HooksFinder.HOOKS_JSON_PATH)) {
            throw new FileNotFoundException(HooksFinder.HOOKS_JSON_PATH + " not found, set FIND_HOOKS to true");
        }

        var gson = new Gson();
        var hooks = gson.fromJson(Files.readString(HooksFinder.HOOKS_JSON_PATH), Hooks.class);
        LOGGER.info("{}", hooks);

        if (!Files.exists(INJECTED_JAR)) {
            Files.createFile(INJECTED_JAR);
            FileUtils.copyInputStreamToFile(Files.newInputStream(JAR_FILE), INJECTED_JAR.toFile());
        }
    }
}
