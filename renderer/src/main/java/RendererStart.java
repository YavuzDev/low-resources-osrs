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
import java.nio.file.Path;

public class RendererStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(RendererStart.class);

    private static final boolean INJECT_HOOKS = false;

    public static void main(String[] args) throws Exception {
        if (INJECT_HOOKS) {
            LOGGER.info("Injecting hooks");
            HooksInjector.main(args);
        }

        var injectedJar = Path.of("resources", "injector", "gamepack-injected.jar");
        if (!Files.exists(injectedJar)) {
            throw new FileNotFoundException("gamepack-injected.jar not found set INJECT_HOOKS to true");
        }

        var config = OsrsConfig.load("http://oldschool83.runescape.com/jav_config.ws");

        var classLoader = new URLClassLoader(new URL[]{injectedJar.toUri().toURL()});
        var clientClass = classLoader.loadClass("client");
        var client = (Applet) clientClass.newInstance();

        var frame = new ClientFrame(800, 600, "Low Resources Bot Client");
        client.setStub(new OsrsAppletStub(config));
        frame.renderGame(client);

    }
}
