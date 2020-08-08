import frame.ClientFrame;
import frame.OsrsAppletStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.OsrsConfig;

import java.applet.Applet;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class RendererStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(RendererStart.class);

    private static final String CONFIG_URL = "http://oldschool83.runescape.com/jav_config.ws";

    private static final String MAIN_CLASS_NAME = "client";

    private static final String TITLE = "Low Resources Bot Client";

    private static final boolean INJECT_HOOKS = false;

    public static void main(String[] args) throws Exception {
        if (INJECT_HOOKS) {
            LOGGER.info("Injecting hooks");
            HooksInjector.main(args);
        }

        if (!Files.exists(HooksInjector.INJECTED_JAR)) {
            throw new FileNotFoundException(HooksInjector.INJECTED_JAR + " not found set INJECT_HOOKS to true");
        }

        var config = OsrsConfig.load(CONFIG_URL);

        LOGGER.info("Loading {} class to render game", MAIN_CLASS_NAME);
        var classLoader = new URLClassLoader(new URL[]{HooksInjector.INJECTED_JAR.toUri().toURL()});
        var clientClass = classLoader.loadClass(MAIN_CLASS_NAME);
        var client = (Applet) clientClass.newInstance();

        LOGGER.info("Initializing frame with title {}", TITLE);
        var frame = new ClientFrame(800, 600, TITLE);
        client.setStub(new OsrsAppletStub(config));
        frame.renderGame(client);
        LOGGER.info("Frame started");
    }
}
