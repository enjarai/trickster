package dev.enjarai.trickster.mixin.client.polymorph;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.pond.PlayerRendererDuck;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T entity);

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void modifyRenderedEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta,
                                      MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                      int light, CallbackInfo ci,
                                      @Local(argsOnly = true) LocalRef<Entity> entityRef,
                                      @Share("disguised") LocalRef<Entity> originalEntity) {
        var component = ModEntityComponents.DISGUISE.getNullable(entityRef.get());
        if (component != null) {
            var disguise = component.getEntityForRendering();
            if (disguise != null) {
                entityRef.set(disguise);
                originalEntity.set(entity);
            }
        }
    }

    // TODO fix this mixin somehow
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    private void transferBipedPose(EntityRenderer<?> instance, Entity entity, float yaw, float tickDelta,
                                   MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                   Operation<Void> original, @Share("disguised") LocalRef<Entity> originalEntity) {

        // If the original entity is a player, and the disguise is any biped, we can do some funny stuff
        //noinspection rawtypes
        if (instance instanceof LivingEntityRenderer<?, ?> livingRenderer
                && livingRenderer.getModel() instanceof BipedEntityModel disguiseBipedModel
                && originalEntity.get() instanceof AbstractClientPlayerEntity player
                && getRenderer(player) instanceof PlayerEntityRenderer playerRenderer) {

            ((PlayerRendererDuck) playerRenderer).trickster$setModelPose(player);
            // We do have to do a slight amount of generics tomfoolery
            // to get this method to accept our other biped model.
            //noinspection unchecked
            playerRenderer.getModel().copyBipedStateTo(disguiseBipedModel);
        }

        original.call(instance, entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
