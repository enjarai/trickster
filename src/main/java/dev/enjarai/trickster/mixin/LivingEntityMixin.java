package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.CollarItem;
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow
    protected abstract double getGravity();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "fall", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyMovementEffects(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"
            )
    )
    private void triggerBoots(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayerEntity player) {
            ItemTriggerHelper.triggerBoots(player, new NumberFragment(this.fallDistance));
        }
    }

    @ModifyReturnValue(
            method = "damage", at = @At("RETURN")
    )
    private boolean cancelDisplacement(boolean original, DamageSource source, float amount) {
        if (original) {
            ModEntityComponents.DISPLACEMENT.get(this).clear();
        }

        return original;
    }

    @Inject(
            method = "tick", at = @At("TAIL")
    )
    private void tickTricksterThings(CallbackInfo ci) {
        removeAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS);
    }

    @ModifyExpressionValue(
            method = "computeFallDamage", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"
            )
    )
    private double modifySafeFallDistance(double original) {
        if (ModEntityComponents.WEIGHT.get(this).getWeight() < 1) {
            var g = getGravity();
            var t = getAttributeValue(EntityAttributes.GENERIC_JUMP_STRENGTH) / g;
            var y = 0.42 * t - (g / 2) * t * t;
            return original + y;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getGravity", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"
            )
    )
    private double modifyGravity(double original) {
        return original * ModEntityComponents.WEIGHT.get(this).getWeight();
    }

    @Inject(
            method = "takeKnockback", at = @At("HEAD")
    )
    private void modifyKnockback(double strength, double x, double z, CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) LocalDoubleRef strengthRef) {
        strengthRef.set(strengthRef.get() * (4 - 3 * ModEntityComponents.WEIGHT.get(this).getWeight()));
    }

    @ModifyExpressionValue(
            method = "getScale", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;getValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"
            )
    )
    private double modifyScale(double original) {
        return original * ModEntityComponents.SCALE.get(this).getScale();
    }

    @ModifyReturnValue(
            method = "getMovementSpeed()F", at = @At("RETURN")
    )
    private float modifySpeed(float original) {
        return (float) (original * ModEntityComponents.SCALE.get(this).getScale());
    }

    @Inject(
            method = "jump",
            at = @At("HEAD")
    )
    private void jump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        CollarItem.playJingleQuestionMark(entity, false);
    }
}
