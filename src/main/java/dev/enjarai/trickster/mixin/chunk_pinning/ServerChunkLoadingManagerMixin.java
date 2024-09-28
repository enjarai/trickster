package dev.enjarai.trickster.mixin.chunk_pinning;

import dev.enjarai.trickster.cca.ModWorldComponents;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkLoadingManager.class)
public class ServerChunkLoadingManagerMixin {
    @Shadow @Final
    ServerWorld world;

    @Inject(
            method = "shouldTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void enableTicksFromWorldPin(ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ModWorldComponents.PINNED_CHUNKS.get(world).isPinned(pos)) {
            cir.setReturnValue(true);
        }
    }
}
