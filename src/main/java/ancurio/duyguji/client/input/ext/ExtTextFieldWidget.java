package ancurio.duyguji.client.input.ext;

import ancurio.duyguji.client.input.AutocompleteWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;

public interface ExtTextFieldWidget {
    static ExtTextFieldWidget from(final Element self) {
        assert(self instanceof ExtTextFieldWidget);
        return (ExtTextFieldWidget) self;
    }

    static boolean isAutocompleteKey(int key) {
        return key == GLFW.GLFW_KEY_TAB || key == GLFW.GLFW_KEY_ENTER;
    }

    void enableAutocomplete(final MinecraftClient client);
    void setAutocompleteOffset(final int x, final int y);
    void setAutocompletePosition(final AutocompleteWindow.Position pos);
    // Should be invoked whenever an autocomplete key was pressed.
    // Returns true if the key event was consumed.
    boolean onAcKeyPressed(int keyCode);
}
