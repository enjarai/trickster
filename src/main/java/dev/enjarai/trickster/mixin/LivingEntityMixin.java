package dev.enjarai.trickster.mixin;

import com.google.common.collect.Multimaps;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow @Final private AttributeContainer attributes;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void checkMovementEvent(CallbackInfo ci) {
        if (!getWorld().isClient && age % 10 == 0) {
            if (SpellCircleEvent.ENTITY_MOVE.fireAllNearby((ServerWorld) getWorld(), getBlockPos(), List.of(
                    EntityFragment.from(this)
            ))) {
                attributes.addTemporaryModifiers(Multimaps.forMap(Map.of(
                        EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE,
                        EntityAttributes.GENERIC_JUMP_STRENGTH, Trickster.NEGATE_ATTRIBUTE
                )));
            } else {
                if (attributes.hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE.id())) {
                    attributes.removeModifiers(Multimaps.forMap(Map.of(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE,
                            EntityAttributes.GENERIC_JUMP_STRENGTH, Trickster.NEGATE_ATTRIBUTE
                    )));
                }
            }
        }
    }
}
