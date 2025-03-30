package dev.enjarai.trickster.render;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModularSpellConstructBlock;
import dev.enjarai.trickster.block.ModularSpellConstructBlockEntity;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ModularSpellConstructBlockEntityRenderer implements BlockEntityRenderer<ModularSpellConstructBlockEntity> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Trickster.id("modular_spell_construct"), "modular_spell_construct");
    public static final Identifier ATLAS_ID = Trickster.id("textures/atlas/modular_spell_construct.png");

    private final ModelPart[] coreModels = new ModelPart[4];

    private final ItemRenderer itemRenderer;
    private final SpellCircleRenderer renderer;

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (int i = 0; i < 4; i++) {
            var x = i % 2;
            var z = i / 2;
            modelPartData.addChild("core_" + i, ModelPartBuilder.create()
                            .uv(0, 0)
                            .cuboid(18f / 2 * x + 2f, 10f, 18f / 2 * z + 2f, 3f, 1f, 3f),
                    ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 16, 16);
    }

    public ModularSpellConstructBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        renderer = new SpellCircleRenderer(false, 1);
        itemRenderer = ctx.getItemRenderer();
        var model = ctx.getLayerModelPart(MODEL_LAYER);
        for (int i = 0; i < 4; i++) {
            coreModels[i] = model.getChild("core_" + i);
        }
    }

    @Override
    public void render(ModularSpellConstructBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var facing = entity.getCachedState().get(ModularSpellConstructBlock.FACING);

        matrices.push();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.translate(-0.5f, -0.5f, -0.5f);

        var knotStack = entity.getStack(0);

        if (!knotStack.isEmpty()) {
            matrices.push();

            matrices.translate(0.5f, 0.8f, 0.5f);
            matrices.scale(0.4f, 0.4f, 0.4f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((entity.age + tickDelta) * 0.1f));

            itemRenderer.renderItem(
                    knotStack, ModelTransformationMode.FIXED,
                    light, overlay, matrices, vertexConsumers,
                    entity.getWorld(), 0
            );

            matrices.pop();
        }

        for (int i = 1; i < entity.size(); i++) {
            var coreStack = entity.getStack(i);
            if (!coreStack.isEmpty()) {
                var textureId = Registries.ITEM.getId(coreStack.getItem()).withPrefixedPath("entity/modular_spell_construct/");
                var spriteId = new SpriteIdentifier(ATLAS_ID, textureId);
                var vertexConsumer = spriteId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
                coreModels[i - 1].render(matrices, vertexConsumer, light, overlay);
            }
        }

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        var normal = new Vec3d(new Vector3f(0, 0, -1)); //.rotate(facing.getRotationQuaternion().rotateX((float) Math.toRadians(90))).mul(-1));//
//                )); // .conjugate()
        // matrices.peek().getNormalMatrix().getNormalizedRotation(new Quaternionf())
        // TODO WTF glisco help!

        for (int i = 1; i < entity.size(); i++) {
            var coreStack = entity.getStack(i);
            matrices.push();

            var executor = entity.executors.get(i - 1);
            if (!coreStack.isEmpty()
                    && executor.isPresent()
                    && !(executor.get() instanceof ErroredSpellExecutor)) {
                float age = entity.age
                    + tickDelta
                    + (entity.getPos().getX()
                            + entity.getPos().getY()
                            + entity.getPos().getZ()
                            + i)
                    * 999;
                var j = i - 1;
                var x = j % 2;
                var z = j / 2;
                matrices.translate((18f / 2 * x + 3.5f) / 16f, (18f / 2 * z + 3.5f) / 16f,
                        0.2f + (float) Math.sin(age * 0.14f) * 0.02f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotation(age / 10));

                this.renderer.renderPartWithoutDrawing(
                        matrices, vertexConsumers, executor.get().spell(),
                        0, 0, 0.2f, 0,
                        tickDelta, size -> 1f, normal
                );
            }

            matrices.pop();
        }
        SpellCircleRenderer.VERTEX_CONSUMERS.draw();

        matrices.pop();
    }
}
