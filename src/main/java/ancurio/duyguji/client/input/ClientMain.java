package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.DuygujiLogger;
import ancurio.duyguji.client.input.api.InputApiInitializer;
import ancurio.duyguji.client.input.api.Shortcode;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientMain implements ClientModInitializer {
    // Sharing this id for logging/etc between submods, don't :concern: me
    public static final String MODID = "duyguji";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public final static DuygujiLogger commonLogger = new DuygujiLogger() {
        public void log(String str, Object ...arg) {
            LOGGER.info("["+ MODID+ "] " + str, arg);
        }
    };

    public static void log(String str, Object ...arg) {
        commonLogger.log(str, arg);
    }

    public static String getConfigPath() {
        return "config/" + MODID;
    }

    public static PatriciaTrie<String> shortcodes;

    private static void initApi() {
        final List<InputApiInitializer> initializers = FabricLoader.getInstance().getEntrypoints("duyguji:input", InputApiInitializer.class);
        for (InputApiInitializer init : initializers) {
            init.onInitialize(null);
        }
    }

    @Override
    public void onInitializeClient() {
        log("Initializing vanilla shortcodes..");

        shortcodes = new PatriciaTrie<String>();

        VanillaShortcodes.read(
            code -> shortcodes.put(":" + code.code + ":", code.symbol)
        );

        log("done.");

        initApi();
    }
}
