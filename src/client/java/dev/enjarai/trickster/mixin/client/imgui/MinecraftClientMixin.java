package dev.enjarai.trickster.mixin.client.imgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import nl.enjarai.cicada.api.imgui.ImMyGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final private Window window;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL"
            )
    )
    private void initImgui(CallbackInfo ci) {
        ImMyGui.init(window.getHandle());
    }
}
