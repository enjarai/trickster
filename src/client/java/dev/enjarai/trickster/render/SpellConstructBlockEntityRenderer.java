package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.SpellConstructBlock;
import dev.enjarai.trickster.block.SpellConstructBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class SpellConstructBlockEntityRenderer implements BlockEntityRenderer<SpellConstructBlockEntity> {
    private final CircleRenderer renderer;
    private final ItemRenderer itemRenderer;

    public SpellConstructBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.renderer = new CircleRenderer(false, false, 4);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(SpellConstructBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(entity.getCachedState().get(SpellConstructBlock.FACING).getRotationQuaternion());
        matrices.translate(-0.5f, -0.5f, -0.5f);

        var knotStack = entity.getStack(0);

        if (!knotStack.isEmpty()) {
            matrices.push();

            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.scale(0.4f, 0.4f, 0.4f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((entity.age + tickDelta) * 0.1f));

            itemRenderer.renderItem(
                knotStack, ModelTransformationMode.FIXED,
                light, overlay, matrices, vertexConsumers,
                entity.getWorld(), 0
            );

            matrices.pop();
        }

        matrices.pop();

        matrices.push();

        var facing = entity.getCachedState().get(SpellConstructBlock.FACING);
        var offset = 1.0 / 16.0 * 5.0;
        matrices.translate(facing.getOffsetX() * -offset, facing.getOffsetY() * -offset, facing.getOffsetZ() * -offset);

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        float age = entity.age + tickDelta + (entity.getPos().getX() + entity.getPos().getY() + entity.getPos().getZ()) * 999;
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(age / 10));
        matrices.translate(
            0, 0,
            (float) Math.sin(age * 0.14f) * 0.02f
        );

        var normal = new Vec3d(new Vector3f(0, 0, -1));

        if (entity.executor != null) {
            this.renderer.renderCircle(
                matrices, entity.executor.spell(),
                0, 0, 0.5f, 0,
                tickDelta, 1, normal, null
            );
            CircleRenderer.VERTEX_CONSUMERS.draw();
        }
        matrices.pop();
    }
}
