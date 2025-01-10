package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class EntityRenderer implements FragmentRenderer<EntityFragment> {
    @Override
    public void render(EntityFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta, SpellCircleRenderer delegator) {
        var entity = fragment.getEntity(MinecraftClient.getInstance().world, true);

        if (entity.isPresent()) {
            matrices.push();

            matrices.translate(x, y, 0);
            matrices.scale(size, delegator.inUI ? -size : size, delegator.inUI ? size : size * 0.01f);
            matrices.scale(0.8f, 0.8f, 0.8f);

            MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                    entity.get(), 0, 0, 0, 0, tickDelta,
                    matrices, vertexConsumers, LightmapTextureManager.pack(0, 15)
            );

            matrices.pop();
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
        }
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
