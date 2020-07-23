package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.Shortcode;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import net.fabricmc.loader.launch.common.FabricLauncher;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

public class VanillaShortcodes {
    private static final Path EDITABLE_PARENT = Paths.get(ClientMain.getConfigPath());
    private static final String EDITABLE_FILENAME = "vanilla.txt";
    private static final String DEFAULT_LOCATION = "assets/duyguji/client/input/vanilla.txt";
    private static final char CONFIG_SEPARATOR = '/';

    public static void read(final BiConsumer<String, String> consumer) {
        try {
            readInternal(consumer);
        } catch (final IOException exc) {
            ClientMain.log("Error reading {}", EDITABLE_FILENAME);
            exc.printStackTrace();
        }
    }

    private static void readInternal(final BiConsumer<String, String> consumer) throws IOException {
        final Path editablePath = getEditablePath();

        final BufferedReader reader = Files.newBufferedReader(editablePath);

        Shortcode.readPairList(reader, CONFIG_SEPARATOR, consumer, ClientMain.commonLogger);
    }

    // Get a path to shortcodes in config/, creating the file from
    // bundled default if necessary.
    private static Path getEditablePath() throws IOException {
        final Path path = EDITABLE_PARENT.resolve(EDITABLE_FILENAME);

        if (Files.isReadable(path)) {
            return path;
        }

        InputStream stream = FabricLauncherBase.getLauncher().getResourceAsStream(DEFAULT_LOCATION);
        Files.createDirectories(EDITABLE_PARENT);
        Files.copy(stream, path);

        return path;
    }
}
