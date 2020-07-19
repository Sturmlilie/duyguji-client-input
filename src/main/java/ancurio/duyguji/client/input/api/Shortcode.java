package ancurio.duyguji.client.input.api;

import ancurio.duyguji.client.input.ClientMain;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * In essence, this is an extended {@code Pair<String, String>}.
 */
public class Shortcode {
    /**
     * The shortcode (without enclosing colons!).
     */
    public final String code;

    /**
     * The represented emoji/symbol; note that this can be more
     * than one codepoint due to combining characters.
     */
    public final String symbol;

    public Shortcode(final String code, final String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    private static Shortcode fromPairLine(final String line, final char separator) {
        final int sepIndex = line.lastIndexOf(separator);

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

    /**
     * @see Shortcode#readPairList(BufferedReader, char, Consumer)
     *
     * @param stream the input stream that lines are sourced from.
     * @param separator the separating character, only the last occurence in a line is considered.
     * @param consumer the callback receiving parsed Shortcodes.
     */
    public static void readPairList(final InputStream stream, final char separator, final Consumer<Shortcode> consumer) {
        readPairList(new BufferedReader(new InputStreamReader(stream)), separator, consumer);
    }

    /**
     * "Pair" referring to Shortcodes.
     * Reads a plain text stream where each line has the format {@code <symbol><separator><code>},
     * filtering out invalid lines, and feeds each successfully parsed Shortcode into
     * {@code consumer}.
     *
     * @param reader the file / data stream that lines are sourced from.
     * @param separator the separating character, only the last occurence in a line is considered.
     * @param consumer the callback receiving parsed Shortcodes.
     */
    public static void readPairList(final BufferedReader reader, final char separator, final Consumer<Shortcode> consumer) {
        reader.lines()
            .map(l -> fromPairLine(l, separator))
            .filter(x -> x != null)
            .forEach(consumer);
    }
}
