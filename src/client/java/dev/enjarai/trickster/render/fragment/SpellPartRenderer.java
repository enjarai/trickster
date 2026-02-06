package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class SpellPartRenderer implements FragmentRenderer<SpellPart> {
    @Override
    public void render(SpellPart fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var renderer = new CircleRenderer(true, false, 3);

        renderer.renderCircle(matrices, fragment, x, y, radius / 4, 0.0f, tickDelta, alpha, normal, null);
    }

    @Override
    public float getProportionalHeight(SpellPart fragment) {
        if (fragment.getSubParts().isEmpty()) {
            // depth of 0
            return 0.25f;
        } else if (fragment.getSubParts().stream().allMatch(x -> x.getSubParts().isEmpty())) {
            // depth of 1
            return 0.25f + 0.25f / 2f;
        } else {
            // anything deeper is roughly this size
            return 0.5f;
        }
    }
}
