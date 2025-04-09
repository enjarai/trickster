package dev.enjarai.trickster.mixin.client.imgui;

import com.mojang.blaze3d.systems.RenderSystem;
import nl.enjarai.cicada.api.imgui.ImGuiThings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(
            method = "flipFrame",
            at = @At("HEAD")
    )
    private static void renderImgui(CallbackInfo ci) {
        ImGuiThings.renderAll();
    }
}
