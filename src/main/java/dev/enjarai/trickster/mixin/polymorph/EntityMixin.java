package dev.enjarai.trickster.mixin.polymorph;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyExpressionValue(
            method = {"handleFallDamage", "isFireImmune", "isInvulnerableTo"},
            at = {
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/Entity;type:Lnet/minecraft/entity/EntityType;"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/Entity;getType()Lnet/minecraft/entity/EntityType;"
                    )
            }
    )
    private EntityType<?> modifyEntityType(EntityType<?> original) {
        var component = ModEntityComponents.DISGUISE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                return disguise.getType();
            }
        }
        return original;
    }
}
