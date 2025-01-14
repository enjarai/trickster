package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.ModularSpellConstructBlock;
import dev.enjarai.trickster.block.ModularSpellConstructBlockEntity;
import dev.enjarai.trickster.block.SpellConstructBlockEntity;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

public class SpellConstructErrorRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();

        if (client.crosshairTarget instanceof BlockHitResult hitResult) {
            var blockEntity = client.world.getBlockEntity(hitResult.getBlockPos());

            if (blockEntity instanceof SpellConstructBlockEntity construct) {
                var y = context.getScaledWindowHeight() / 2 - 10;

                if (construct.getComponents().contains(DataComponentTypes.CUSTOM_NAME)) {
                    y = draw(client, context, construct.getComponents().get(DataComponentTypes.CUSTOM_NAME), y);
                    y += 3;
                }

                if (construct.executor instanceof ErroredSpellExecutor executor) {
                    draw(client, context, executor.errorMessage(), y);
                }
            }

            if (blockEntity instanceof ModularSpellConstructBlockEntity modularConstruct) {
                ModularSpellConstructBlock.getSlotForHitPos(hitResult, client.world.getBlockState(modularConstruct.getPos())).ifPresent(i -> {
                    var stack = modularConstruct.getStack(i);
                    var y = context.getScaledWindowHeight() / 2 - 10;

                    if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        y = draw(client, context, stack.get(DataComponentTypes.CUSTOM_NAME), y);
                        y += 3;
                    }

                    if (i > 0) {
                        var executor = modularConstruct.executors.get(i - 1);
                        if (executor.isPresent()
                                && executor.get() instanceof ErroredSpellExecutor errored) {
                            draw(client, context, errored.errorMessage(), y);
                        }
                    }
                });
            }
        }
    }

    private static int draw(MinecraftClient client, DrawContext context, Text errorMessage, int y) {
        for (OrderedText orderedText : client.textRenderer.wrapLines(errorMessage, context.getScaledWindowWidth() / 3)) {
            context.drawText(
                    client.textRenderer, orderedText, context.getScaledWindowWidth() / 2 + 10,
                    y, 0xffffff, true
            );
            y += 9;
        }
        return y;
    }
}
