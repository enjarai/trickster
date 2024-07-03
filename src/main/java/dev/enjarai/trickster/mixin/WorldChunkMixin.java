package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.block.ShadowBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends Chunk {
    public WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "setBlockEntity(Lnet/minecraft/block/entity/BlockEntity;)V", at = @At(value = "HEAD"), cancellable = true)
    private void setBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof ShadowBlockEntity shadowBlockEntity) {
            BlockPos blockPos = shadowBlockEntity.getPos();
            BlockState blockState = this.getBlockState(blockPos);
            BlockState blockState2 = shadowBlockEntity.getCachedState();

            if (blockState != blockState2) {
                shadowBlockEntity.setCachedState(blockState);
            }

            shadowBlockEntity.setWorld(this.getWorld());
            shadowBlockEntity.cancelRemoval();
            BlockEntity blockEntity2 = this.blockEntities.put(blockPos.toImmutable(), shadowBlockEntity);

            if (blockEntity2 != null && blockEntity2 != shadowBlockEntity) {
                blockEntity2.markRemoved();
            }

            ci.cancel();
        }
    }
}
