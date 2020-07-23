package ancurio.duyguji.client.input.api;

import java.io.BufferedReader;
import java.util.function.BiConsumer;

/**
 * Analogue to {@code Pair<String, String>}.
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

    public Shortcode(final String symbol, final String code) {
        this.symbol = symbol;
        this.code = code;
    }

    private static void parsePairLine(final String line, final char separator, final BiConsumer<String, String> consumer,
                                      final DuygujiLogger logger) {
        final int sepIndex = line.lastIndexOf(separator);

        if (sepIndex == -1) {
            logger.log("Rejecting [{}]: no slash", line);
            return;
        }

        final String symbol = line.substring(0, sepIndex);
        final String code = line.substring(sepIndex+1, line.length());

        if (symbol.isEmpty() || code.isEmpty()) {
            logger.log("Rejecting [{}]: symbol/code empty", line);
            return;
        }

        consumer.accept(symbol, code);
    }

    /**
     * "Pair" referring to Shortcodes.
     * Reads a plain text stream where each line has the format {@code <symbol><separator><code>},
     * filtering out invalid lines, and feeds each successfully parsed Shortcode into
     * {@code consumer}.
     *
     * @param reader the file / data stream that lines are sourced from, opened in UTF-8 mode.
     * @param separator the separating character, only the last occurence in a line is considered.
     * @param consumer the callback receiving parsed symbol/shortcode pairs.
     * @param logger an optional logger for non-fatal parsing errors.
     */
    public static void readPairList(final BufferedReader reader, final char separator, final BiConsumer<String, String> consumer,
                                    final DuygujiLogger logger) {
        // Pass a dummy logger if user provided null, so we only deal with this case once
        final DuygujiLogger loggerImpl;
        if (logger == null) {
            loggerImpl = new DuygujiLogger() {
                public void log(String str, Object ...arg) {}
            };
        } else {
            loggerImpl = logger;
        }

        reader.lines().forEach(line -> parsePairLine(line, separator, consumer, logger));
    }
}
