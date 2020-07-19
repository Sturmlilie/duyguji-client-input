package ancurio.duyguji.client.input.api;

import ancurio.duyguji.client.input.ClientMain;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class Shortcode {
    // The shortcode (without enclosing colons!)
    public final String code;

    // The represented emoji/symbol; note that this can be more
    // than one codepoint due to combining characters
    public final String symbol;

    public Shortcode(final String code, final String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String colonShortcode() {
        return ":" + code + ":";
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

    public static void readPairList(final InputStream stream, final char separator, final Consumer<Shortcode> consumer) {
        readPairList(new BufferedReader(new InputStreamReader(stream)), separator, consumer);
    }

    public static void readPairList(final BufferedReader reader, final char separator, final Consumer<Shortcode> consumer) {
        reader.lines()
            .map(l -> fromPairLine(l, separator))
            .filter(x -> x != null)
            .forEach(consumer);
    }
}
