import com.google.gson.Gson;
import hook.Hooks;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HooksInjector {

    private static final boolean FIND_HOOKS = false;

    private static final boolean DOWNLOAD_JAR = false;

    public static void main(String[] args) throws Exception {
        if (FIND_HOOKS) {
            HooksFinder.main(args);
        }
        var jarUrl = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";
        var jarFile = Path.of("resources", "injector", "gamepack.jar");
        if (DOWNLOAD_JAR) {
            FileUtils.copyURLToFile(new URL(jarUrl), jarFile.toFile());
        }

        var hooksJsonPath = Path.of("resources", "hooks", "hooks.json");
        if (!Files.exists(hooksJsonPath)) {
            throw new FileNotFoundException("hooks.json not found, set FIND_HOOKS to true");
        }

        var gson = new Gson();
        var hooks = gson.fromJson(Files.readString(hooksJsonPath), Hooks.class);
        System.out.println(hooks);

//        var injectedPath = jarFile.getParent().resolve("gamepack-injected.jar");
//        if (!Files.exists(injectedPath)) {
//            Files.createFile(injectedPath);
//            FileUtils.copyInputStreamToFile(Files.newInputStream(jarFile), injectedPath.toFile());
//        }
    }
}
