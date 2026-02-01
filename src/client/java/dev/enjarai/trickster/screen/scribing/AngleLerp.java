package dev.enjarai.trickster.screen.scribing;

import io.wispforest.owo.braid.animation.Lerp;
import net.minecraft.util.math.MathHelper;

public class AngleLerp extends Lerp<Double> {
    protected AngleLerp(Double start, Double end) {
        super(start, end);
    }

    @Override
    protected Double at(double t) {
        if (start - end >= Math.PI) {
            return MathHelper.lerp(t, start, end + Math.PI * 2) % (Math.PI * 2);
        } else if (end - start > Math.PI) {
            return MathHelper.lerp(t, start + Math.PI * 2, end) % (Math.PI * 2);
        } else {
            return MathHelper.lerp(t, start, end);
        }
    }
}
