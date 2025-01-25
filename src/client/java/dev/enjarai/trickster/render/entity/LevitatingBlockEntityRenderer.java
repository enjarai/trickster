package dev.enjarai.trickster.render.entity;

import dev.enjarai.trickster.entity.LevitatingBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;

public class LevitatingBlockEntityRenderer extends EntityRenderer<LevitatingBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public LevitatingBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(LevitatingBlockEntity fallingBlockEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState = fallingBlockEntity.getBlockState();
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            World world = fallingBlockEntity.getWorld();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                var spinnyRandom = new LocalRandom(fallingBlockEntity.getUuid().getMostSignificantBits());
                var totalAge = fallingBlockEntity.age + tickDelta;

                matrixStack.push();
                BlockPos blockPos = BlockPos.ofFloored(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
                matrixStack.translate(0, 0.5, 0);
                matrixStack.multiply(new Quaternionf().rotationXYZ(
                        totalAge * (spinnyRandom.nextFloat() - 0.5f),
                        0, //totalAge * (spinnyRandom.nextFloat() - 0.5f),
                        totalAge * (spinnyRandom.nextFloat() - 0.5f)
                ));
                matrixStack.translate(-0.5, -0.5, -0.5);
                this.blockRenderManager
                        .getModelRenderer()
                        .render(
                                world,
                                this.blockRenderManager.getModel(blockState),
                                blockState,
                                blockPos,
                                matrixStack,
                                vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                                false,
                                Random.create(),
                                blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos()),
                                OverlayTexture.DEFAULT_UV
                        );
                matrixStack.pop();
                super.render(fallingBlockEntity, f, tickDelta, matrixStack, vertexConsumerProvider, i);
            }
        }
    }

    @Override
    public Identifier getTexture(LevitatingBlockEntity fallingBlockEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
