package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.PlayerAnimationComponent;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class PlayerAnimator {
    public static void setAngles(PlayerEntity player, PlayerAnimationComponent animation, float tickDelta, ModelPart rightArm, ModelPart leftArm) {
        var hatTakey = MathHelper.lerp(tickDelta, animation.prevHatTakeyNess, animation.hatTakeyNess);

        if (player.getMainArm() == Arm.RIGHT) {
            leftArm.pitch = leftArm.pitch * (1 - hatTakey) - hatTakey * 2.1f;
        } else {
            rightArm.pitch = rightArm.pitch * (1 - hatTakey) - hatTakey * 2.1f;
        }
    }
}
