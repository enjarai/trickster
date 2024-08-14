package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.BlockHitResult;

public class CircleErrorRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();

        if (client.crosshairTarget instanceof BlockHitResult hitResult &&
                client.world.getBlockEntity(hitResult.getBlockPos()) instanceof SpellCircleBlockEntity blockEntity &&
                blockEntity.lastError != null) {
            context.drawText(
                    client.textRenderer, blockEntity.lastError,
                    context.getScaledWindowWidth() / 2 + 10, context.getScaledWindowHeight() / 2 - 10,
                    0xffffff, true
            );
        }
    }
}
