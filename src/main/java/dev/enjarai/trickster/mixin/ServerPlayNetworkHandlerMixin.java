package dev.enjarai.trickster.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.enjarai.trickster.cca.SharedManaComponent;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(
        method = "onClientCommand(Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/RideableInventory;openInventory(Lnet/minecraft/entity/player/PlayerEntity;)V"
        )
    )
    private void subscribeInventory(ClientCommandC2SPacket packet, CallbackInfo ci) {
        SharedManaComponent.INSTANCE.subscribeInventory(this.player, Optional.empty());
    }
}
