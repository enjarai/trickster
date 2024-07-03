package dev.enjarai.trickster.render;

import dev.enjarai.trickster.block.ShadowBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class ShadowBlockEntityRenderer implements BlockEntityRenderer<ShadowBlockEntity> {
    public ShadowBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(ShadowBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(entity.disguise.getDefaultState(), matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }
}
