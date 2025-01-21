package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.ChargingArrayBlockEntity;
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

public class ChargingArrayBlockEntityRenderer implements BlockEntityRenderer<ChargingArrayBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ChargingArrayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ChargingArrayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(entity.getCachedState().get(SpellConstructBlock.FACING).getRotationQuaternion());

        for (int i = 0; i < entity.size(); i++) {
            var stack = entity.getStack(i);

            var x = (i % 3 - 1) / 16f * 15f;
            //noinspection IntegerDivisionInFloatingPointContext
            var y = (i / 3 - 1) / 16f * 15f;

            matrices.push();

            matrices.translate(x / 3f, -0.2f, y / 3f);
            matrices.scale(0.35f, 0.35f, 0.35f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((entity.age + tickDelta) * 0.1f));

            itemRenderer.renderItem(
                    stack, ModelTransformationMode.FIXED,
                    light, overlay, matrices, vertexConsumers,
                    entity.getWorld(), 0
            );

            matrices.pop();
        }

        matrices.pop();
    }
}
