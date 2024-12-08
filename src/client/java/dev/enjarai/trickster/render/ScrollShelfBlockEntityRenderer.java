package dev.enjarai.trickster.render;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ScrollShelfBlock;
import dev.enjarai.trickster.block.ScrollShelfBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import static dev.enjarai.trickster.block.ScrollShelfBlock.GRID_HEIGHT;
import static dev.enjarai.trickster.block.ScrollShelfBlock.GRID_WIDTH;

public class ScrollShelfBlockEntityRenderer implements BlockEntityRenderer<ScrollShelfBlockEntity> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Trickster.id("scroll_shelf"), "scroll_shelf");
    public static final Identifier ATLAS_ID = Trickster.id("textures/atlas/scroll_shelf.png");

    private final ModelPart[] scrollModels = new ModelPart[GRID_WIDTH * GRID_HEIGHT];

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (int i = 0; i < GRID_WIDTH * GRID_HEIGHT; i++) {
            var x = i % GRID_WIDTH;
            var y = i / GRID_HEIGHT;
            modelPartData.addChild("scroll_" + i, ModelPartBuilder.create()
                            .uv(0, 0)
                            .cuboid(15f / GRID_WIDTH * x + 1.5f, 15f / GRID_HEIGHT * y + 1f, 16f, 3f, 3f, 1f),
                    ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 16, 16);
    }

    public ScrollShelfBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        var model = ctx.getLayerModelPart(MODEL_LAYER);
        for (int i = 0; i < GRID_WIDTH * GRID_HEIGHT; i++) {
            scrollModels[i] = model.getChild("scroll_" + i);
        }
    }

    @Override
    public void render(ScrollShelfBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var facing = entity.getCachedState().get(ScrollShelfBlock.FACING);
        //noinspection DataFlowIssue
        light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().offset(facing));

        matrices.push();
        matrices.translate(0.5f, 0, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.getPositiveHorizontalDegrees()));
        matrices.translate(-0.5f, 0, -0.5f);

        for (int i = 0; i < GRID_WIDTH * GRID_HEIGHT; i++) {
            var stack = entity.getStack(i);
            if (!stack.isEmpty()) {
                var textureId = Registries.ITEM.getId(stack.getItem()).withPrefixedPath("entity/scroll_shelf/");
                var spriteId = new SpriteIdentifier(ATLAS_ID, textureId);
                var vertexConsumer = spriteId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
                scrollModels[i].render(matrices, vertexConsumer, light, overlay);
            }
        }

        if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHit
                && blockHit.getBlockPos().equals(entity.getPos())
                && blockHit.getSide() == entity.getCachedState().get(ScrollShelfBlock.FACING)) {
            var slot = ScrollShelfBlock.getSlotForHitPos(blockHit, entity.getCachedState());
            if (slot.isPresent()) {
                var stack = entity.getStack(slot.getAsInt());
                if (!stack.isEmpty()) {
                    renderLabel(entity, tickDelta, matrices, vertexConsumers, light, slot.getAsInt(), stack.getName());
                }
            }
        }

        matrices.pop();
    }

    public void renderLabel(ScrollShelfBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int slot, Text name) {
        matrices.push();
        matrices.translate(0, 0, 1.1f);

        var pixel = 1f / 16f;
        var x = pixel * 15f / GRID_WIDTH * (slot % GRID_WIDTH) + pixel * 3f;
        //noinspection IntegerDivisionInFloatingPointContext
        var y = pixel * 15f / GRID_HEIGHT * (slot / GRID_HEIGHT) + pixel * 7f;
        matrices.translate(x, y, 0);

        matrices.scale(0.018F, -0.018F, 0.018F);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        MinecraftClient client = MinecraftClient.getInstance();
        float opacity = client.options.getTextBackgroundOpacity(0.25F);
        int backgroundColor = (int) (opacity * 255.0F) << 24;
        TextRenderer textRenderer = client.textRenderer;
        float offsetX = (float) (-textRenderer.getWidth(name) / 2);

//        textRenderer.draw(name, offsetX, 0, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, backgroundColor, light);
        textRenderer.draw(name, offsetX, 0, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }
}
