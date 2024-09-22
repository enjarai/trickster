package dev.enjarai.trickster.mixin.client.animation;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.render.PlayerAnimator;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModelMixin<T> {
    @Override
    protected void positionModelParts(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            PlayerAnimator.setAngles(player, ModEntityCumponents.PLAYER_ANIMATION.get(player), tickDelta, rightArm, leftArm);
        }
    }
}
