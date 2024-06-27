package dev.enjarai.trickster.mixin.client;


import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerRenderer {
    @Unique
    public SpellCircleRenderer trickster$renderer = new SpellCircleRenderer();

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void trickster$onRender(AbstractClientPlayerEntity player, float $$1, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int $$5, CallbackInfo ci) {
        var spell = trickster$get_spell(player);
        if (spell.isPresent()) {
            matrices.push();
            //translate to be at eye level
            matrices.translate(0f, player.getEyeHeight(player.getPose()), 0f);

            //rotate to match direction player is facing
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(player.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180 +  player.getPitch()));

            //push forward from eyes a bit
            matrices.translate(0f, 0f, -1f);

            this.trickster$renderer.renderPart(matrices, vertexConsumers, spell, 0, 0, 0.5f, 0, tickDelta, size -> 1f);
            matrices.pop();
        }
    }

    @Unique
    private Optional<SpellPart> trickster$get_spell(PlayerEntity entity) {
        var mainHandSpell = entity.getStackInHand(Hand.MAIN_HAND).get(ModComponents.SPELL);
        var offHandSpell = entity.getStackInHand(Hand.OFF_HAND).get(ModComponents.SPELL);

        if (mainHandSpell != null) {
            return Optional.of(mainHandSpell.spell());
        } else if (offHandSpell != null) {
            return Optional.of(offHandSpell.spell());
        } else {
            return Optional.empty();
        }
    }
}
