package ancurio.duyguji.client.mixin;

import ancurio.duyguji.client.ext.ExtTextFieldWidget;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement {
    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    public void onKeyPressed(final int keyCode, final int scanCode, final int modifiers,
                             final CallbackInfoReturnable ci) {
        if (keyCode != GLFW.GLFW_KEY_TAB) {
            return;
        }

        final Element focused = this.getFocused();

        if (!(focused instanceof ExtTextFieldWidget)) {
            return;
        }

        ExtTextFieldWidget ext = ExtTextFieldWidget.from(focused);

        if (ext.onTabPressed()) {
            ci.setReturnValue(true);
        }
    }
}
