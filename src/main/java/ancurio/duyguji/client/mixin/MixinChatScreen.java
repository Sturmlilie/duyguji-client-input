package ancurio.duyguji.client.mixin;

import ancurio.duyguji.client.ext.ExtTextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen extends Screen {
	@Shadow
	protected TextFieldWidget chatField;

    private MixinChatScreen() {
        super(null);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onSetup(final CallbackInfo ci) {
        ExtTextFieldWidget ext = ExtTextFieldWidget.from(this.chatField);
        ext.enableAutocomplete(this.client);
        ext.setAutocompleteOffset(-2, -2);
    }

    // Need this a second time because ChatScreen is stupid and delegates
    // to CommandSuggestor before super
    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    public void onKeyPressed(final int keyCode, final int scanCode, final int modifiers,
                             final CallbackInfoReturnable ci) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            ExtTextFieldWidget ext = ExtTextFieldWidget.from(this.chatField);

            if (ext.onTabPressed()) {
                ci.setReturnValue(true);
            }
        }
    }
}
