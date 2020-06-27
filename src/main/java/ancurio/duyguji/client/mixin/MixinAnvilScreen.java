package ancurio.duyguji.client.mixin;

import ancurio.duyguji.client.ext.ExtTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen extends Screen {
	@Shadow
	private TextFieldWidget nameField;

    private MixinAnvilScreen() {
        super(null);
    }

    @Inject(at = @At("TAIL"), method = "setup")
    public void onSetup(final CallbackInfo ci) {
        ExtTextFieldWidget ext = ExtTextFieldWidget.from(this.nameField);
        ext.enableAutocomplete(this.client);
        ext.setAutocompleteOffset(0, -4);
    }
}
