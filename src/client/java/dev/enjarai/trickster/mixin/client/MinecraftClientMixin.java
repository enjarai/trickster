package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.WandLeftClickPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    private int itemUseCooldown;

    // Cast the wand on left-click this allows for spamming left click which is the same as using an item normally.
    @Inject(method = "doAttack", at = @At(value = "HEAD"), cancellable = true)
    private void wandLeftClickDetection(CallbackInfoReturnable<Boolean> cir) {
        Hand hand = getHand();
        if (player.getStackInHand(hand).getItem() == ModItems.WAND) {
            cir.setReturnValue(true);
            castWand(hand);
        }
    }

    // Cast the wand when holding left-click uses the same cooldown as using an item normally
    @Inject(method = "handleBlockBreaking", at = @At(value = "HEAD"), cancellable = true)
    private void wandLeftClickHoldDetection(boolean breaking, CallbackInfo ci) {
        if (!breaking) {
            return;
        }
        Hand hand = getHand();
        if (player.getStackInHand(hand).getItem() == ModItems.WAND) {
            ci.cancel();
            if (itemUseCooldown == 0) {
                castWand(hand);
            }
        }
    }

    @Unique
    private Hand getHand() {
        return player.getMainHandStack().isEmpty() ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    @Unique
    private void castWand(Hand hand) {
        itemUseCooldown = 4;
        player.swingHand(hand);
        ModNetworking.CHANNEL.clientHandle().send(new WandLeftClickPacket(hand));
    }
}
