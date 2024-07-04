package dev.enjarai.trickster.cca;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ShadowDisguiseMapComponent implements AutoSyncedComponent {
    private static final KeyedEndec<Int2ObjectOpenHashMap<Block>> DISGUISES = Endec.map(
        Endec.INT,
        MinecraftEndecs.ofRegistry(Registries.BLOCK)
    ).xmap(Int2ObjectOpenHashMap::new, map -> map).keyed("disguises", Int2ObjectOpenHashMap::new);

    private Int2ObjectOpenHashMap<Block> disguises = new Int2ObjectOpenHashMap<>();
    private final Chunk chunk;

    public ShadowDisguiseMapComponent(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(DISGUISES, disguises);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        disguises = tag.get(DISGUISES);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(DISGUISES.endec(), disguises);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        disguises = buf.read(DISGUISES.endec());
    }

    /**
     * Assumes the pos is inside this chunk and gets its corresponding disguise, if available.
     */
    @Nullable
    public BlockState getFunnyState(BlockPos pos) {
        var block = disguises.get(encodePos(pos));
        if (block != null) {
            return block.getDefaultState();
        }
        return null;
    }

    @Nullable
    public BlockState getFunnyState(int x, int y, int z) {
        var block = disguises.get(encodePos(x, y, z));
        if (block != null) {
            return block.getDefaultState();
        }
        return null;
    }

    public boolean setFunnyState(BlockPos pos, Block block) {
        if (disguises.put(encodePos(pos), block) != block) {
            chunk.setNeedsSaving(true);
            ModChunkCumponents.SHADOW_DISGUISE_MAP.sync(chunk);
            return true;
        }

        return false;
    }

    public boolean clearFunnyState(BlockPos pos) {
        if (disguises.remove(encodePos(pos)) != null) {
            chunk.setNeedsSaving(true);
            ModChunkCumponents.SHADOW_DISGUISE_MAP.sync(chunk);
            return true;
        }

        return false;
    }

    public int encodePos(BlockPos pos) {
        return encodePos(pos.getX(), pos.getY(), pos.getZ());
    }

    public int encodePos(int x, int y, int z) {
        var xe = x & 15;
        var ze = (z & 15) << 4;
        var ye = y << 8;
        return ye | ze | xe;
    }
}
