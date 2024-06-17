package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.Trickster;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyExpressionValue(
            method = "getFovMultiplier",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Float;isInfinite(F)Z"
            )
    )
    private boolean fixFovWhenFrozen(boolean original) {
        return original || getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE.id());
    }
}
