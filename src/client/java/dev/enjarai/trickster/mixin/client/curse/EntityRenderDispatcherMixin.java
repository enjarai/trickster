package dev.enjarai.trickster.mixin.client.curse;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(
            method = "render", at = @At("HEAD")
    )
    private void modifyRenderedEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, CallbackInfo ci,
            @Local(argsOnly = true) LocalRef<Entity> entityRef,
            @Share("disguised") LocalRef<Entity> originalEntity) {
        var component = ModEntityComponents.CURSE.getNullable(entityRef.get());
        if (component != null) {
            var disguise = component.getEntityForRendering();
            if (disguise != null) {
                entityRef.set(disguise);
                originalEntity.set(entity);
            }
        }
    }
}
