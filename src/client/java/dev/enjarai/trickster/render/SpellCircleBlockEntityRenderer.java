package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class SpellCircleBlockEntityRenderer implements BlockEntityRenderer<SpellCircleBlockEntity> {
    private final SpellCircleRenderer renderer;

    public SpellCircleBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.renderer = new SpellCircleRenderer(false);
    }

    @Override
    public void render(SpellCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        this.renderer.renderPart(matrices, vertexConsumers, Optional.of(entity.spell), 0, 0, 0.5f, 0, tickDelta, size -> 1f, new Vec3d(0, 0, -1));
//        matrices.scale(1, -1, 1);
//        this.renderer.renderPart(matrices, vertexConsumers, Optional.of(entity.spell), 0, 0, 1, 0, tickDelta, size -> 1f);
        matrices.pop();
    }
}
