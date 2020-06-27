package ancurio.duyguji.client.mixin;

import ancurio.duyguji.client.AutocompleteWindow;
import ancurio.duyguji.client.ClientMain;
import ancurio.duyguji.client.ext.ExtTextFieldWidget;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget extends AbstractButtonWidget implements ExtTextFieldWidget {
    private MixinTextFieldWidget() {
        super(0, 0, 0, 0, null);
    }

    @Shadow
    @Final
    private TextRenderer textRenderer;

    @Shadow
    protected abstract String getText();

    @Shadow
    protected abstract int getCursor();

    @Shadow
    protected abstract void setText(String text);

    @Shadow
    protected abstract void setCursor(int index);

    // The god-bool that turns emote autocomplete on for this instance
    private boolean provideAutocomplete = false;

    // Offset at which to draw the autocomplete suggestion window
    private int acOffsetX = 0;
    private int acOffsetY = 0;
    private AutocompleteWindow.Position relativePos = AutocompleteWindow.Position.ABOVE;

    private boolean charChangedFlag = false;
    private boolean ignoreUpdatesFlag = false;
    private AutocompleteWindow acWindow;
    private AutocompleteWindow.Data acData = AutocompleteWindow.Data.EMPTY;
    private int acWindowTextOffset = 0;
    private boolean showAcWindow = false;

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    public void onKeyPressed(final int keyCode, final int scanCode, final int modifiers,
                             final CallbackInfoReturnable ci) {

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_RIGHT:
            case GLFW.GLFW_KEY_BACKSPACE:
            case GLFW.GLFW_KEY_DELETE:
            charChangedFlag = true;
            break;
        }

        if (!shouldShowSuggestions()) {
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_UP:
            acData.moveSelection(-1);
            break;

            case GLFW.GLFW_KEY_DOWN:
            acData.moveSelection(1);
            break;

            case GLFW.GLFW_KEY_TAB:
            onTabPressed();
            break;

            default:
            return;
        }

        ci.setReturnValue(true);
    }

    @Inject(at = @At("RETURN"), method = "setCursor")
    public void setCursor(final CallbackInfo ci) {
        if (!provideAutocomplete || ignoreUpdatesFlag) {
            return;
        }

        if (charChangedFlag = true) {
            acData = updateAutocomplete();
            charChangedFlag = false;
        } else {
            acData = AutocompleteWindow.Data.EMPTY;
        }
    }

    // Setting this flag here means we only trigger on keyboard input (exclusing copy/paste).
    @Inject(at = @At("HEAD"), method = "charTyped(CI)Z")
    public void onCharTyped(final char chr, final int keyCode, final CallbackInfoReturnable ci) {
        charChangedFlag = true;
    }

    @Inject(at = @At("RETURN"), method = "write")
    public void write(final CallbackInfo ci) {
        if (!provideAutocomplete || ignoreUpdatesFlag) {
            return;
        }

        if (charChangedFlag = true) {
            acData = updateAutocomplete();
            charChangedFlag = false;
        } else {
            acData = AutocompleteWindow.Data.EMPTY;
        }
    }

    @Inject(at = @At("TAIL"), method = "renderButton")
    public void onRenderButton(final MatrixStack matrices,
                               final int mouseX, final int mouseY,
                               final float delta, final CallbackInfo ci) {
        if (shouldShowSuggestions()) {
            final int x = this.x + acOffsetX + acWindowTextOffset;
            final int y = this.y + acOffsetY;
            acWindow.render(matrices, acData, x, y);
        }
    }

    // ExtTextFieldWidget
    public void enableAutocomplete(final MinecraftClient client) {
        provideAutocomplete = true;

        // Choose some default colors that work with ChatScreen
        final int bgColor = client.options.getTextBackgroundColor(Integer.MIN_VALUE);
        final int bgSelectionColor = 0x00FF6600 | (bgColor & 0xFF000000);
        acWindow = new AutocompleteWindow(textRenderer, bgColor, bgSelectionColor);
    }

    public void setAutocompleteOffset(final int x, final int y) {
        acOffsetX = x;
        acOffsetY = y;
    }

    public void setAutocompletePosition(final AutocompleteWindow.Position pos) {
        this.relativePos = pos;
    }

    public void onTabPressed() {
        if (!provideAutocomplete || !shouldShowSuggestions()) {
            return;
        }

        applySuggestion(acData.selectedSymbol());
        acData = AutocompleteWindow.Data.EMPTY;
    }

    // Utility
    private void applySuggestion(final String symbol) {
        final String inputLine = this.getText().trim();
        final int cursor = this.getCursor();

        assert(atEndOfWord(inputLine, cursor));

        final int start = getStartOfHalfTypedWord(inputLine, cursor);
        final String front = inputLine.substring(0, start);
        final String back = inputLine.substring(cursor, inputLine.length());

        final String replacedLine = front + symbol + back;

        ignoreUpdatesFlag = true;
        this.setText(replacedLine);
        this.setCursor(start+symbol.length());
        ignoreUpdatesFlag = false;
    }

    private static boolean atEndOfWord(final CharSequence str, final int cursor) {
        // Cursor at beginning?
        if (cursor == 0) {
            return false;
        }

        final int len = str.length();

        // Past any trimmed whitespace?
        if (cursor > len) {
            return false;
        }

        // Not trailing a non-space character?
        if (str.charAt(cursor-1) == ' ') {
            return false;
        }

        // Inside a word?
        if (cursor < len && str.charAt(cursor) != ' ') {
            return false;
        }

        return true;
    }

    // Contract: atEndOfWord(str, cursor) == true
    private static int getStartOfHalfTypedWord(final CharSequence str, final int cursor) {
        int closestSpace = cursor-1;
        while (closestSpace >= 0 && str.charAt(closestSpace) != ' ') {
            closestSpace--;
        }

        return closestSpace+1;
    }

    private AutocompleteWindow.Data updateAutocomplete() {
        final String inputLine = this.getText().trim();
        final int cursor = this.getCursor();

        if (!atEndOfWord(inputLine, cursor)) {
            return AutocompleteWindow.Data.EMPTY;
        }

        final int start = getStartOfHalfTypedWord(inputLine, cursor);
        final String halfWord = inputLine.substring(start, cursor);
        final Map<String, String> view = ClientMain.shortcodes.prefixMap(halfWord);

        if (view.size() > 8) {
            return AutocompleteWindow.Data.EMPTY;
        }

        final List<Map.Entry<String, String>> suggestions = new ArrayList<Map.Entry<String, String>>(view.size());

        for (final Map.Entry<String, String> entry : view.entrySet()) {
            // Do LRO sorting here
            suggestions.add(entry);
        }

        AutocompleteWindow.Data data = new AutocompleteWindow.Data();
        data.suggestions = suggestions;
        data.selectionIndex = suggestions.size()-1;

        if (start > 0) {
            acWindowTextOffset = textRenderer.getWidth(inputLine.substring(0, start));
        } else {
            acWindowTextOffset = 0;
        }

        return data;
    }

    private boolean shouldShowSuggestions() {
        if (!provideAutocomplete) {
            return false;
        }

        return !acData.suggestions.isEmpty();
    }
}
