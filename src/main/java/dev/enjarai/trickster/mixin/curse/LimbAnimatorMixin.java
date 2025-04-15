package dev.enjarai.trickster.mixin.curse;

import dev.enjarai.trickster.pond.LimbAnimatorDuck;
import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LimbAnimator.class)
public class LimbAnimatorMixin implements LimbAnimatorDuck {
    @Shadow
    private float prevSpeed;

    @Shadow
    private float speed;

    @Shadow
    private float pos;

    @Override
    public void trickster$copyFrom(LimbAnimator animator) {
        var castyMcCastFace = (LimbAnimatorDuck) animator;
        prevSpeed = castyMcCastFace.trickster$getPrevSpeed();
        speed = castyMcCastFace.trickster$getSpeed();
        pos = castyMcCastFace.trickster$getPos();
    }

    @Override
    public float trickster$getPrevSpeed() {
        return prevSpeed;
    }

    @Override
    public float trickster$getSpeed() {
        return speed;
    }

    @Override
    public float trickster$getPos() {
        return pos;
    }
}
