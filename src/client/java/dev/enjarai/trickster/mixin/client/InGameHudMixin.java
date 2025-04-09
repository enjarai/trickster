package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.DisplacementComponent;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.TrickHatItem;
import dev.enjarai.trickster.pond.QuackingInGameHud;
import dev.enjarai.trickster.render.FunnyStaticFrameBufferThing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin implements QuackingInGameHud {
    @Inject(method = "renderVignetteOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"))
    private void changeColorWhenFrozen(DrawContext context, Entity entity, CallbackInfo ci) {
        if (
            entity instanceof LivingEntity livingEntity
                    && livingEntity.getAttributes().hasModifierForAttribute(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Trickster.NEGATE_ATTRIBUTE.id()
                    )
        ) {
            context.setShaderColor(0.4f, 0.4f, 0f, 1f);
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"))
    private void teleportationVignette(DrawContext context, Entity entity, CallbackInfo ci) {
        if (entity == null) {
            return;
        }

        var grace = ModEntityComponents.GRACE.get(entity);
        if (!grace.isInGrace("displacement")) {
            return;
        }

        var progress = (DisplacementComponent.CHARGE_TICKS - grace.getGraceState("displacement")) / (float) DisplacementComponent.CHARGE_TICKS;
        var color = RenderSystem.getShaderColor();

        context.setShaderColor(
                MathHelper.lerp(progress, color[0], 1f),
                MathHelper.lerp(progress, color[1], 1f),
                MathHelper.lerp(progress, color[2], 0f),
                MathHelper.lerp(progress, color[3], 1f)
        );
    }

    @Unique
    private float animationOffset;

    @Inject(
            method = "renderHotbar", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"
            )
    )
    private void renderHatHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local PlayerEntity player) {
        var hatStack = player.getOffHandStack();
        if (hatStack.isIn(ModItems.HOLDABLE_HAT)) {
            FunnyStaticFrameBufferThing.updateSize(MinecraftClient.getInstance());
            var deltaAnimationOffset = MathHelper.lerp(
                    tickCounter.getTickDelta(false),
                    animationOffset, animationOffset - animationOffset / 4
            );
            int roundedAnimationOffset = deltaAnimationOffset < 0 ? MathHelper.ceil(deltaAnimationOffset) : MathHelper.floor(deltaAnimationOffset);

            var matrices = context.getMatrices();
            matrices.push();
            matrices.translate(0, 0, -500);

            var middle = context.getScaledWindowWidth() / 2;
            var x = player.getMainArm() == Arm.RIGHT ? middle - 109 - 8 : middle + 109 - 8;
            var y = context.getScaledWindowHeight() - 40;

            for (int i = 0; i < 5; i++) {
                var offset = i - 2 - roundedAnimationOffset;
                var offsetOffset = offset + deltaAnimationOffset;
                var scrollStack = TrickHatItem.getScrollRelative(hatStack, offset);

                matrices.push();
                matrices.translate(0, 0, -Math.abs(offsetOffset));

                var brightness = MathHelper.lerp(Math.clamp(Math.abs(offsetOffset / 2), 0, 1), 1f, 0.0f);

                var buf = FunnyStaticFrameBufferThing.THING.get();
                buf.clear(MinecraftClient.IS_SYSTEM_MAC);
                buf.beginWrite(false);
                context.drawItem(scrollStack, (int) (x + offsetOffset * 8), y);
                context.draw();
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);

                FunnyStaticFrameBufferThing.drawFunnily(matrices, brightness, brightness, brightness, brightness);

                matrices.pop();
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
        }
    }

    @Inject(
            method = "tick()V", at = @At("TAIL")
    )
    private void tickHatHud(CallbackInfo ci) {
        animationOffset -= animationOffset / 4;
    }

    @Override
    public void trickster$scrollTheHat(int delta) {
        animationOffset += delta;
    }
}
