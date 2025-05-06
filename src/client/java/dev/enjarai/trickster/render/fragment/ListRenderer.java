package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import static dev.enjarai.trickster.render.SpellCircleRenderer.CIRCLE_TEXTURE;

public class ListRenderer implements FragmentRenderer<ListFragment> {
    @Override
    public void render(ListFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
            SpellCircleRenderer delegator) {
        var i = 0;
        for (var f : fragment.fragments()) {
            i++;

            float rate = (float) (i / Math.PI);

            float localRadius = radius / rate * 0.3f;

            float localX = (float) (x + Math.sin(i) * radius / rate);
            float localY = (float) (y + Math.cos(i) * radius / rate);

            SpellCircleRenderer.drawTexturedQuad(
                    matrices, vertexConsumers, CIRCLE_TEXTURE,
                    localX - localRadius, localX + localRadius, localY - localRadius, localY + localRadius,
                    0,
                    delegator.getR(), delegator.getG(), delegator.getB(),
                    delegator.getCircleTransparency(), delegator.inUI
            );

            //noinspection rawtypes
            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(f.type()));

            if (renderer != null) {
                //noinspection unchecked
                renderer.render(f, matrices, vertexConsumers, localX, localY, localRadius, 1, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(f, matrices, vertexConsumers, localX, localY, localRadius, 1);
            }
        }
    }
}
