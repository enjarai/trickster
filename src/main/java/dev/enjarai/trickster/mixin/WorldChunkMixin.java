package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.cca.ModChunkCumponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.RebuildChunkPacket;
import dev.enjarai.trickster.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Shadow
    final World world;

    public WorldChunkMixin(World world) {
        this.world = world;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;", at = @At("HEAD"))
    private void dispelShadowBlock(BlockPos pos, BlockState newState, boolean moved, CallbackInfoReturnable cir) {
        if (world.isClient) return;

        if (newState != world.getBlockState(pos)) {
            var serverWorld = world.getServer().getWorld(world.getRegistryKey());
            var chunk = ((WorldChunk)(Object)this);
            if (chunk instanceof EmptyChunk) return;
            var map = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);

            if (map.clearFunnyState(pos)) {
                ModNetworking.CHANNEL.serverHandle(serverWorld, pos).send(new RebuildChunkPacket(pos));

                var particlePos = pos.toCenterPos();
                serverWorld.spawnParticles(
                        ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                        1, 0, 0, 0, 0
                );
            }
        }
    }
}
