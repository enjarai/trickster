package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

public class SpellFleckRenderer implements FleckRenderer<SpellFleck> {
    private final SpellCircleRenderer renderer = new SpellCircleRenderer(false, 1);

    @Override
    public void render(SpellFleck fleck, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {

    }
}
