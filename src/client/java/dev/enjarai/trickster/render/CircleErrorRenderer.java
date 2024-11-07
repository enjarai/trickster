package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.BlockHitResult;

public class CircleErrorRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();

        if (client.crosshairTarget instanceof BlockHitResult hitResult &&
                client.world.getBlockEntity(hitResult.getBlockPos()) instanceof SpellCircleBlockEntity blockEntity &&
                blockEntity.getComponents().get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent component &&
                component.executor() instanceof ErroredSpellExecutor executor) {
            context.drawText(
                    client.textRenderer, executor.errorMessage(),
                    context.getScaledWindowWidth() / 2 + 10, context.getScaledWindowHeight() / 2 - 10,
                    0xffffff, true
            );
        }
    }
}
