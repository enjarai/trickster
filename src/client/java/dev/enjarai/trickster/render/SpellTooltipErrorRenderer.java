package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.ModularSpellConstructBlock;
import dev.enjarai.trickster.block.ModularSpellConstructBlockEntity;
import dev.enjarai.trickster.block.SpellConstructBlockEntity;
import dev.enjarai.trickster.entity.SpellDisplayingEntity;
import dev.enjarai.trickster.entity.SpellRunningState;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class SpellTooltipErrorRenderer {
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

                if (construct.getComponents().get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent component
                        && component.executor() instanceof ErroredSpellExecutor executor) {
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

                    if (stack.get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent component
                            && component.executor() instanceof ErroredSpellExecutor executor) {
                        draw(client, context, executor.errorMessage(), y);
                    }
                });
            }
        }

        else {
            var position = client.player.getEyePos();
            var look = client.player.getRotationVec(tickCounter.getTickDelta(true));
            var multipliedDirection = position.add(look.multiply(4d));
            var hit = ProjectileUtil.raycast(client.player, position, multipliedDirection, new Box(position, multipliedDirection), always -> true, 9);

            if (hit instanceof EntityHitResult entityHitResult
                    && entityHitResult.getEntity() instanceof SpellDisplayingEntity spellDisplaying
                    && spellDisplaying.getRunningState() instanceof SpellRunningState.Error error) {

                draw(client, context, error.error(), context.getScaledWindowHeight() / 2 - 10);
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
