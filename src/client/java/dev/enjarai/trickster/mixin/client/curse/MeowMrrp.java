package dev.enjarai.trickster.mixin.client.curse;

import dev.enjarai.trickster.cca.CurseComponent;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MeowMrrp {
    @Shadow
    protected TextFieldWidget chatField;

    @Inject(
            at = @At("HEAD"), method = "onChatFieldUpdate", cancellable = true
    )
    private void meow(String chatText, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null &&
                ModEntityComponents.CURSE.get(MinecraftClient.getInstance().player).getCurrentCurse() == CurseComponent.Curse.MEOW_MRRP) {
            String meow = CurseComponent.Curse.meowify(chatText);
            if (!meow.equals(chatText)) { // no stack overflows please! // dang thats totally not cursed :3
                chatField.setText(meow);
                ci.cancel();
            }
        }
    }
}
