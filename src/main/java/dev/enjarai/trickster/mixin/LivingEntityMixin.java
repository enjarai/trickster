package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModChunkCumponents;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import org.jetbrains.annotations.Nullable;
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
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract AttributeContainer getAttributes();

    @Unique
    private static final StatusEffectInstance PERM_BLINDNESS = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 2);
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
            method = "tick",
            at = @At("TAIL")
    )
    private void tickTricksterThings(CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity) {
            inShadowBlock = inShadowBlock(getWorld(), BlockPos.ofFloored(this.getEyePos()));
        }

        setAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS, null);

        if (!ModEntityCumponents.GRACE.get(this).isInGrace("scale")) {
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

        var shadowBlocks = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);
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
}
