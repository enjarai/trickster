package dev.enjarai.trickster.pond;

import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;

public interface EntityDisguiseDuck {
    void trickster$setLimbAnimator(LimbAnimator animator);

    SoundEvent trickster$getHurtSound(DamageSource source);
}
