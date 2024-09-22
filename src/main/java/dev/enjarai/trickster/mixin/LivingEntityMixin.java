package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModChunkComponents;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.pond.DirectlyDamageDuck;
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.enjarai.trickster.spell.trick.entity.SetScaleTrick.SCALE_ID;

@SuppressWarnings("UnstableApiUsage")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DirectlyDamageDuck {
    @Unique
    private static final StatusEffectInstance PERM_BLINDNESS = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 2);

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow public abstract DamageTracker getDamageTracker();

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract float getHealth();


    @Shadow protected float lastDamageTaken;
    @Shadow public int maxHurtTime;
    @Shadow public int hurtTime;
    @Shadow @Final public LimbAnimator limbAnimator;

    @Shadow public abstract boolean isDead();

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    @Shadow public abstract void onDeath(DamageSource damageSource);

    @Shadow public abstract void playSound(@Nullable SoundEvent sound);

    @Shadow protected abstract void playHurtSound(DamageSource damageSource);

    @Unique
    private boolean inShadowBlock;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(
            method = "hasStatusEffect",
            at = @At("RETURN")
    )
    public boolean hasStatusEffect(boolean original, RegistryEntry<StatusEffect> effect) {
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity))
            return original;

        if (effect == StatusEffects.BLINDNESS) {
            return original || inShadowBlock;
        }

        return original;
    }

    @Nullable
    @ModifyReturnValue(
            method = "getStatusEffect",
            at = @At("RETURN")
    )
    public StatusEffectInstance getStatusEffect(StatusEffectInstance original, RegistryEntry<StatusEffect> effect) {
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity))
            return original;

        if (effect == StatusEffects.BLINDNESS && original == null && inShadowBlock) {
            return PERM_BLINDNESS;
        }

        return original;
    }

    @Inject(
            method = "fall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyMovementEffects(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"
            )
    )
    private void triggerBoots(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if ((LivingEntity)(Object)this instanceof ServerPlayerEntity player) {
            ItemTriggerHelper.triggerBoots(player, new NumberFragment(this.fallDistance));
        }
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void tickTricksterThings(CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity) {
            inShadowBlock = inShadowBlock(getWorld(), BlockPos.ofFloored(this.getEyePos()));
        }

        setAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS, null);

        if (!ModEntityComponents.GRACE.get(this).isInGrace("scale")) {
            // Handle slow scaling reset
            var currentScale = 0d;
            if (getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_SCALE, SCALE_ID)) {
                currentScale = getAttributes().getModifierValue(EntityAttributes.GENERIC_SCALE, SCALE_ID);
            }
            var newScale = currentScale;

            if (currentScale < -0.01 || currentScale > 0.01) {
                newScale -= currentScale * 0.001;
            } else {
                newScale = 0;
            }

            if (newScale != currentScale) {
                getAttributes().getCustomInstance(EntityAttributes.GENERIC_SCALE)
                        .overwritePersistentModifier(new EntityAttributeModifier(SCALE_ID, newScale, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    @Unique
    private static boolean inShadowBlock(World world, BlockPos blockPos) {
        var chunk = world.getChunk(blockPos);

        if (chunk instanceof EmptyChunk)
            return false;

        var shadowBlocks = ModChunkComponents.SHADOW_DISGUISE_MAP.get(chunk);
        var funnyState = shadowBlocks.getFunnyState(blockPos);

        return funnyState != null && funnyState.isSolidBlock(world, blockPos);
    }

    @Inject(
            method = "tryUseTotem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"
            )
    )
    private void detectTotemUsage(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        setAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS, true);
    }

    @Override
    public boolean trickster$damageDirectly(DamageSource source, float amount) {
        this.getWorld().sendEntityDamage(this, source);

        this.lastDamageTaken = amount;
        this.timeUntilRegen = 20;

        this.getDamageTracker().onDamage(source, amount);
        this.setHealth(this.getHealth() - amount);
//        this.setAbsorptionAmount(this.getAbsorptionAmount() - amount);
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE);

        this.maxHurtTime = 10;
        this.hurtTime = this.maxHurtTime;

        this.limbAnimator.setSpeed(1.5F);

        if (this.isDead()) {
            this.playSound(this.getDeathSound());
            this.onDeath(source);
        } else {
            this.playHurtSound(source);
        }

        return true;
    }
}
