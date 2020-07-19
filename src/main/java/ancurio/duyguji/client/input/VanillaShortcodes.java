package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.Shortcode;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.loader.launch.common.FabricLauncher;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

public class VanillaShortcodes {
    private static final Path EDITABLE_PARENT = Paths.get(ClientMain.getConfigPath());
    private static final String EDITABLE_FILENAME = "vanilla.txt";
    private static final String DEFAULT_LOCATION = "assets/" + ClientMain.MODID + "/client/input/vanilla.txt";
    private static final char CONFIG_SEPARATOR = '/';

    public static List<Shortcode> read() {
        try {
            return readInternal();
        } catch (final IOException exc) {
            ClientMain.log("Error reading {}", EDITABLE_FILENAME);
            exc.printStackTrace();
            return new ArrayList<Shortcode>();
        }
    }

    private static List<Shortcode> readInternal() throws IOException {
        final Path editablePath = getEditablePath();

        final BufferedReader reader = Files.newBufferedReader(editablePath);

        final List<Shortcode> codes = reader.lines()
            .map(VanillaShortcodes::lineToCode)
            .filter(x -> x != null)
            .collect(Collectors.toList());

        return codes;
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

    private static Shortcode lineToCode(final String line) {
        final int sepIndex = line.lastIndexOf(CONFIG_SEPARATOR);

        if (sepIndex == -1) {
            ClientMain.log("Rejecting [{}]: no slash", line);
            return null;
        }

        final String symbol = line.substring(0, sepIndex);
        final String code = line.substring(sepIndex+1, line.length());

        if (symbol.isEmpty() || code.isEmpty()) {
            ClientMain.log("Rejecting [{}]: symbol/code empty", line);
            return null;
        }

        return new Shortcode(code, symbol);
    }
}
