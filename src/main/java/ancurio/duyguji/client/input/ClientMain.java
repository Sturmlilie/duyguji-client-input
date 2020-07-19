package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.Shortcode;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientMain implements ClientModInitializer {
    // Sharing this id for logging/etc between submods, don't :concern: me
    public static final String MODID = "duyguji";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void log(String str, Object ...arg) {
        LOGGER.info("["+ MODID+ "] " + str, arg);
    }

    public static String getConfigPath() {
        return "config/" + MODID;
    }

    public static PatriciaTrie<String> shortcodes;

    @Override
    public void onInitializeClient() {
        final List<Shortcode> vanillaCodes = VanillaShortcodes.read();

        shortcodes = new PatriciaTrie<String>();

        for (final Shortcode code : vanillaCodes) {
            shortcodes.put(code.colonShortcode(), code.symbol);
        }

        log("Vanilla shortcodes initialized");
    }
}
