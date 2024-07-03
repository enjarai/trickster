package dev.enjarai.trickster.cca;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ShadowDisguiseMapComponent implements AutoSyncedComponent {
    private static final KeyedEndec<Object2ObjectOpenHashMap<BlockPos, Block>> DISGUISES = Endec.map(
        MinecraftEndecs.BLOCK_POS,
        MinecraftEndecs.ofRegistry(Registries.BLOCK)
    ).xmap(Object2ObjectOpenHashMap::new, map -> map).keyed("disguises", Object2ObjectOpenHashMap::new);

    private Object2ObjectOpenHashMap<BlockPos, Block> value = new Object2ObjectOpenHashMap<>();
    private final Chunk chunk;

    public ShadowDisguiseMapComponent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Object2ObjectOpenHashMap<BlockPos, Block> value() {
        return value;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(DISGUISES, value);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        value = tag.get(DISGUISES);
    }
}
