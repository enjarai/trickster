package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.SpellCircleBlock;
import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Optional;

public class SpellCircleBlockEntityRenderer implements BlockEntityRenderer<SpellCircleBlockEntity> {
    private final SpellCircleRenderer renderer;

    public SpellCircleBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.renderer = new SpellCircleRenderer(false, 1);
    }

    @Override
    public void render(SpellCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        var facing = entity.getCachedState().get(SpellCircleBlock.FACING);
        var offset = 1.0 / 16.0 * 7.0;
        matrices.translate(facing.getOffsetX() * -offset, facing.getOffsetY() * -offset, facing.getOffsetZ() * -offset);

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        int age = entity.age + (entity.getPos().getX() + entity.getPos().getY() + entity.getPos().getZ()) * 999;
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) Math.sin((age + tickDelta) * 0.1f) * 0.05f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.sin((age + tickDelta) * 0.12f) * 0.05f));
        matrices.translate(
                0, 0,
                (float) Math.sin((age + tickDelta) * 0.14f) * 0.02f
        );

        var normal = new Vec3d(new Vector3f(0, 0, -1).rotate(facing.getRotationQuaternion().conjugate()));

        if (entity.spell != null) {
            this.renderer.renderPart(
                    matrices, vertexConsumers, entity.spell,
                    0, 0, 0.5f, 0,
                    tickDelta, size -> 1f, normal
            );
        }
        matrices.pop();
    }
}
