package ancurio.duyguji.client.input.api;

/**
 * Entrypoint for mods wishing to register custom shortcodes
 * at runtime. Keeping a reference to {@code registry} and
 * dynamically registering lists after the initializer returns is allowed.
 * Declare your implementation in {@code fabric.mod.json} under
 * {@code "duyguji:input"}.
 */
@FunctionalInterface
public interface InputApiInitializer {
    void onInitialize(final ShortcodeListRegistry registry);
}
