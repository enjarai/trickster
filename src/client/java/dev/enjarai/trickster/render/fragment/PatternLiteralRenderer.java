package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static dev.enjarai.trickster.render.SpellCircleRenderer.PATTERN_TO_PART_RATIO;

public class PatternLiteralRenderer implements FragmentRenderer<Pattern> {
    public static final Identifier OVERLAY_TEXTURE = Trickster.id("textures/gui/pattern_literal.png");

    @Override
    public void render(Pattern fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        PatternRenderer.renderPattern(fragment, matrices, vertexConsumers, x, y, size / PATTERN_TO_PART_RATIO, alpha, delegator);
        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, OVERLAY_TEXTURE,
            x - size, x + size, y - size, y + size,
            0,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }

    @Override
    public boolean renderRedrawDots() {
        return false;
    }
}
