package ancurio.duyguji.client.input.api;

import java.util.Collection;

/**
 * Main interface for registering and updating
 * lists of shortcodes at runtime. Think of it as a key into a big
 * {@code HashTable<Key, ListOfShortcodes>} on the input module's side.
 * Initially, the list represented is empty. To fill it,
 * first call {@code beginUpdate()}, which will implicitly clear any
 * previous entries. Then call {@code putEntry(symbol, code)} with every
 * shortcode you wish to register. Finish and commit the update with a
 * call to {@code endUpdate()}.
 * The implementation of the convenience method {@code update} provides
 * an example on this process.
 */
public interface ShortcodeList {
    /**
     * Convenience method that performs the three update steps in one
     * go and inserts {@code codes} as the new entries in this list.
     *
     * @param codes the list of shortcodes to register for this key.
     */
    default void update(final Collection<Shortcode> codes) {
        beginUpdate();
        for (final Shortcode sc : codes) {
            putEntry(sc.symbol, sc.code);
        }
        endUpdate();
    }

    /**
     * Begin updating the list.
     * This implicitly erases any previous entries.
     */
    void beginUpdate();

    /**
     * Append a new entry to the shortcode list.
     * Calling this outside of a begin/end update cycle results in
     * undefined behavior
     *
     * @param symbol the symbol or emoji represented by this shortcode.
     * @param code the shortcode string, without surrounding colons.
     */
    void putEntry(final String symbol, final String code);

    /**
     * Signals the end of the update process. No more entries can be
     * added after this call before calling {@code beginUpdate} again.
     */
    void endUpdate();
}
