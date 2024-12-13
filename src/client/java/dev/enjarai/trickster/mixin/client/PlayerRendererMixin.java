package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
    @Unique
    public SpellCircleRenderer trickster$renderer = new SpellCircleRenderer(false, 1);

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(
                "HEAD"
            )
    )
    public void trickster$onRender(
            AbstractClientPlayerEntity player, float $$1, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int $$5, CallbackInfo ci
    ) {
        var spell = trickster$get_spell(player);
        if (spell.isPresent() && (player != MinecraftClient.getInstance().player || MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson())) {
            matrices.push();
            //translate to be at eye level
            matrices.translate(0f, player.getEyeHeight(player.getPose()), 0f);

            //rotate to match direction player is facing
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(player.getYaw(tickDelta)));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(player.getPitch(tickDelta)));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

            //push forward from eyes a bit
            matrices.translate(0f, 0f, 1f);

            var rot = new Vec3d(-1, -1, player.getRotationVector().y);;

            this.trickster$renderer.renderPart(matrices, vertexConsumers, spell.get(), 0, 0, 0.5f, 0, tickDelta, size -> 1f, rot);
            matrices.pop();
        }
    }

    @Unique
    private Optional<SpellPart> trickster$get_spell(PlayerEntity entity) {
        var mainHandStack = entity.getMainHandStack();
        var offHandStack = entity.getOffHandStack();

        var mainHandSpell = mainHandStack.get(ModComponents.FRAGMENT);
        var offHandSpell = offHandStack.get(ModComponents.FRAGMENT);

        if (entity.getComponent(ModEntityComponents.IS_EDITING_SCROLL).isEditing()) {
            if (mainHandStack.isIn(ModItems.SCROLLS) && mainHandStack.get(ModComponents.FRAGMENT) != null && mainHandSpell != null) {
                return Optional.of(mainHandSpell.value()).filter(v -> v instanceof SpellPart).map(n -> (SpellPart) n);
            } else if (offHandStack.isIn(ModItems.SCROLLS) && offHandStack.get(ModComponents.FRAGMENT) != null && offHandSpell != null) {
                return Optional.of(offHandSpell.value()).filter(v -> v instanceof SpellPart).map(n -> (SpellPart) n);
            }
        }

        return Optional.empty();
    }
}
