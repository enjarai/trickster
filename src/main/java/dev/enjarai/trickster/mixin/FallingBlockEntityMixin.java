package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.KnotItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "handleFallDamage", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private void crackKnots(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) float f) {
        if (f > 0 && getWorld() instanceof ServerWorld serverWorld) {
            for (var knot : serverWorld.getOtherEntities(this, getBoundingBox(), e -> e instanceof ItemEntity)) {
                doAnvilSmash((ItemEntity) knot, serverWorld);
            }
        }
    }

    @Unique
    private void doAnvilSmash(ItemEntity entity, ServerWorld serverWorld) {
        ItemStack thisItemStack = entity.getStack();

        if (thisItemStack.getItem() instanceof KnotItem knotItem) {
            Vec3d position = entity.getPos();

            // Delete the destroyed item
            entity.remove(Entity.RemovalReason.DISCARDED);

            var advancementTriggerBox = Box.from(getPos()).expand(8);
            var nearbyPlayers = serverWorld.getPlayers(p -> advancementTriggerBox.contains(p.getPos()));

            // Spawn the resulting item stack in the world
            var crackedVersion = knotItem.getCrackedVersion();
            if (crackedVersion != null) {
                var crackedStack = crackedVersion.createStack(serverWorld);
                crackedStack = knotItem.transferPropertiesToCracked(serverWorld, thisItemStack, crackedStack);

                ItemEntity itemEntity = new ItemEntity(serverWorld, position.x, position.y, position.z, crackedStack);
                serverWorld.spawnEntity(itemEntity);

                nearbyPlayers.forEach(p -> ModCriteria.CRACK_KNOT.trigger(p, knotItem));
            } else {
                nearbyPlayers.forEach(ModCriteria.DESTROY_KNOT::trigger);
            }

            // Play sound
            float randomVolume = 1.0F + serverWorld.getRandom().nextFloat() * 0.2F;
            float randomPitch = 0.9F + serverWorld.getRandom().nextFloat() * 0.2F;
            serverWorld.playSound(null, position.x, position.y, position.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, randomVolume, randomPitch);
        }
    }
}
