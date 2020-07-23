package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.DuygujiLogger;
import ancurio.duyguji.client.input.api.InputApiInitializer;
import ancurio.duyguji.client.input.api.Shortcode;
import ancurio.duyguji.client.input.api.ShortcodeList;
import ancurio.duyguji.client.input.api.ShortcodeListRegistry;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientMain implements ClientModInitializer {
    public static final String LOG_NAMESPACE = "duyguji:input";
    public static final Logger LOGGER = LogManager.getLogger(LOG_NAMESPACE);

    public final static DuygujiLogger commonLogger = new DuygujiLogger() {
        public void log(String str, Object ...arg) {
            LOGGER.info("["+ LOG_NAMESPACE+ "] " + str, arg);
        }
    };

    public static void log(String str, Object ...arg) {
        commonLogger.log(str, arg);
    }

    public static String getConfigPath() {
        return "config/duyguji";
    }

    public static ShortcodeStorage storage;

    private static void initApi(final ShortcodeListRegistry registry) {
        final List<InputApiInitializer> initializers = FabricLoader.getInstance().getEntrypoints("duyguji:input", InputApiInitializer.class);
        for (InputApiInitializer init : initializers) {
            init.onInitialize(registry);
        }
    }

    @Override
    public void onInitializeClient() {
        log("Initializing vanilla shortcodes..");

        storage = new ShortcodeStorage();
        final ShortcodeList vanillaList = storage.register("minecraft", "mc");
        vanillaList.beginUpdate();

        VanillaShortcodes.read(
            code -> vanillaList.putEntry(code)
        );

        vanillaList.endUpdate();
        log("done.");

        initApi(storage);
    }
}
