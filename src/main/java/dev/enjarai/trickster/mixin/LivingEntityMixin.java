package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static dev.enjarai.trickster.spell.trick.entity.SetScaleTrick.SCALE_ID;

@SuppressWarnings("UnstableApiUsage")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract AttributeContainer getAttributes();

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTricksterThings(CallbackInfo ci) {
        removeAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS);

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
}
