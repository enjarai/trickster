package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubtitlesHud.class)
public class SubtitlesHudMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void offsetSubtitles(DrawContext context, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var barsComponent = player.getComponent(ModEntityCumponents.BARS);
        var bars = barsComponent.getBars();

        if (Trickster.CONFIG.barsHorizontal()) {
            context.getMatrices().translate(0f, bars.size() * -8f, 0f);
        } else {
            context.getMatrices().translate(bars.size() * -8f, 0f, 0f);
        }
    }
}
