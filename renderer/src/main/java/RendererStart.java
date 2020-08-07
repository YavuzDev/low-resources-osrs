import frame.ClientFrame;
import frame.OsrsAppletStub;
import org.apache.commons.io.FileUtils;
import util.OsrsConfig;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class RendererStart {

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        var jarUrl = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";
        var jarFile = Path.of("resources", "render", "gamepack.jar");
        FileUtils.copyURLToFile(new URL(jarUrl), jarFile.toFile());

        var config = OsrsConfig.load("http://oldschool83.runescape.com/jav_config.ws");

        var classLoader = new URLClassLoader(new URL[]{jarFile.toUri().toURL()});
        var clientClass = classLoader.loadClass("client");
        var client = (Applet) clientClass.newInstance();

        var frame = new ClientFrame(800, 600, "Low Resources Bot Client");
        client.setStub(new OsrsAppletStub(config));
        frame.renderGame(client);
    }
}
