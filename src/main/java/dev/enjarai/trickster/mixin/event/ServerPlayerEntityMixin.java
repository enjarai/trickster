package dev.enjarai.trickster.mixin.event;

import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"
            )
    )
    private void triggerItemSpell(Entity target, CallbackInfo ci) {
        ItemTriggerHelper.triggerMainHand((ServerPlayerEntity)(Object)this, EntityFragment.from(target));
    }
}
