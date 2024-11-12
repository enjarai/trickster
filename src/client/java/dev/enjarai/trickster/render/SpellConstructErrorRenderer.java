package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.ModularSpellConstructBlock;
import dev.enjarai.trickster.block.ModularSpellConstructBlockEntity;
import dev.enjarai.trickster.block.SpellConstructBlockEntity;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

public class SpellConstructErrorRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();

        if (client.crosshairTarget instanceof BlockHitResult hitResult) {
            var blockEntity = client.world.getBlockEntity(hitResult.getBlockPos());

            if (blockEntity instanceof SpellConstructBlockEntity construct
                    && construct.getComponents().get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent component
                    && component.executor() instanceof ErroredSpellExecutor executor) {
                draw(client, context, executor.errorMessage());
            }

            if (blockEntity instanceof ModularSpellConstructBlockEntity modularConstruct) {
                ModularSpellConstructBlock.getSlotForHitPos(hitResult, client.world.getBlockState(modularConstruct.getPos())).ifPresent(i -> {
                    if (modularConstruct.getStack(i).get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent component
                            && component.executor() instanceof ErroredSpellExecutor executor) {
                        draw(client, context, executor.errorMessage());
                    }
                });
            }
        }
    }

    private static void draw(MinecraftClient client, DrawContext context, Text errorMessage) {
        context.drawText(
                client.textRenderer, errorMessage,
                context.getScaledWindowWidth() / 2 + 10, context.getScaledWindowHeight() / 2 - 10,
                0xffffff, true
        );
    }
}
