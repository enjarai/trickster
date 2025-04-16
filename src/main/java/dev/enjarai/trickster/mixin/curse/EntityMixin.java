package dev.enjarai.trickster.mixin.curse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyExpressionValue(
            method = { "handleFallDamage", "isFireImmune", "isInvulnerableTo" }, at = {
                    @At(
                            value = "FIELD", target = "Lnet/minecraft/entity/Entity;type:Lnet/minecraft/entity/EntityType;"
                    ),
                    @At(
                            value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getType()Lnet/minecraft/entity/EntityType;"
                    )
            }
    )
    private EntityType<?> modifyEntityType(EntityType<?> original) {
        if ((Object) this instanceof PlayerEntity) {
            var entity = ModEntityComponents.CURSE.get(this).getEntity();
            if (entity != null) {
                return entity.getType();
            }
        }
        return original;
    }
}
