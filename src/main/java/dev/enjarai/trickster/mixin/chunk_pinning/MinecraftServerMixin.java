package dev.enjarai.trickster.mixin.chunk_pinning;

import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.cca.ModWorldComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getPersistentStateManager()Lnet/minecraft/world/PersistentStateManager;"))
    private void loadPinChunks(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci, @Local(ordinal = 1) ServerWorld serverWorld) {
        ModWorldComponents.PINNED_CHUNKS.get(serverWorld).pinThemAll();
    }
}
