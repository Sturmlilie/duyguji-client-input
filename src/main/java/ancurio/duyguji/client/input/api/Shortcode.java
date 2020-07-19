package ancurio.duyguji.client.input.api;

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
}
