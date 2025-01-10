package dev.enjarai.trickster.render.blot;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.blot.SpellBlot;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class SpellBlotRenderer implements BlotRenderer<SpellBlot> {
    private final SpellCircleRenderer renderer = new SpellCircleRenderer(true, 1);

    @Override
    public void render(SpellBlot fleck, @Nullable SpellBlot lastFleck, DrawContext context, RenderTickCounter tickCounter, int color) {
        var tickDelta = tickCounter.getTickDelta(true);

        var position = fleck.pos();
        var size = fleck.size();
        var spell = fleck.spell();

        var oldPosition = fleck.pos();
        var oldSize = fleck.size();

        if (lastFleck != null) {
            oldPosition = lastFleck.pos();
            oldSize = lastFleck.size();
        }

        var targetPosition = oldPosition.lerp(position, tickDelta, new Vector2f());
        var targetSize = MathHelper.lerp(tickDelta, oldSize, size);;

        renderer.renderPart(
                context.getMatrices(),
                context.getVertexConsumers(),
                spell,
                targetPosition.x() + context.getScaledWindowWidth() / 2d,
                targetPosition.y() + context.getScaledWindowHeight() / 2d,
                targetSize * 10,
                0,
                tickDelta,
                s -> 1.0f,
                new Vec3d(-1, 0, 0)
        );
    }
}
