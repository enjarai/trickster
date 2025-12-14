package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import static net.minecraft.util.math.ColorHelper.Argb.*;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.Random;

public class ColorRenderer implements FragmentRenderer<ColorFragment> {
    static final Identifier TEXTURE = Trickster.id("textures/gui/square.png");
    static final Identifier BACK_TEXTURE = Trickster.id("textures/gui/square_back.png");

    static int tick = 0;
    static float lastDt = 0;

    @Override
    public void render(ColorFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
            SpellCircleRenderer delegator) {
        var col = fragment.color();
        float time = System.currentTimeMillis() % 1048576 / 50f;
        size /= 4;
        int r = getRed(col), g = getGreen(col), b = getBlue(col), a = getAlpha(col);
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(size, size, delegator.inUI ? size : -size);
        for (int i = 0; i < 8; i++) {
            var rand = new Random(((int) (time / 5) + i) * 1000);
            var rotv = rand.nextFloat() - 0.5f;
            var rot = rand.nextFloat() - 0.5f * (float) Math.PI * 2;
            rotv *= 10;
            var age = (7 - i + time / 5 % 1) / 8;
            var scale = 1 - (2 * age - 1) * (2 * age - 1);

            var fade = age * age;

            matrices.push();
            matrices.multiply(new Quaternionf(new AxisAngle4f(rot + age * rotv, 0, 0, 1)));
            matrices.scale(scale, scale, 1);
            SpellCircleRenderer.drawTexturedQuad(
                    matrices, vertexConsumers, TEXTURE,
                    -1, 1, -1, 1,
                    0.1f - age * 0.01f,
                    MathHelper.lerp(fade, r / 255f, 1),
                    MathHelper.lerp(fade, g / 255f, 1),
                    MathHelper.lerp(fade, b / 255f, 1),
                    alpha * delegator.getCircleTransparency() * a / 255f, delegator.inUI
            );
            SpellCircleRenderer.drawTexturedQuad(
                    matrices, vertexConsumers, BACK_TEXTURE,
                    -1, 1, -1, 1,
                    0.05f - age * 0.01f,
                    1, 1, 1,
                    alpha * delegator.getCircleTransparency(), delegator.inUI
            );
            matrices.pop();
        }
        matrices.pop();
    }
}
