package ancurio.duyguji.client.input.api;

/**
 * Main API context object for registering shortcodes.
 */
public interface ShortcodeListRegistry {
    /**
     * Register a new shortcode list. Note that while {@code namespace}
     * must be unique during runtime, {@code shortname} does not have to
     * be and is meant to be useful to the player for disambiguation in
     * case of duplicate shortcodes. The input module may decide to further
     * modify any provided shortcodes.
     *
     * @param namespace a unique string used for internal book-keeping purposes.
     * @param shortname a string that may be used for conflict resolution.
     *
     * @return a handle to the registered list.
     */
    ShortcodeList register(final String namespace, final String shortname);
}
