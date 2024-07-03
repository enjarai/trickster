package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.block.ShadowBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ShadowDisguiseComponent implements AutoSyncedComponent {
    private final ShadowBlockEntity entity;
    private Block value = Blocks.AIR;

    public ShadowDisguiseComponent(ShadowBlockEntity entity) {
        this.entity = entity;
    }

    public Block value() {
        return value;
    }

    public void value(Block block) {
        value = block;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("disguise")) {
            var value = tag.get("disguise");
            var result = Block.CODEC.decoder().decode(NbtOps.INSTANCE, value).result();
            result.ifPresent(blockNbtElementPair -> this.value = blockNbtElementPair.getFirst());
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (value != null && value != Blocks.AIR) {
            var result = Block.CODEC.encoder().encodeStart(NbtOps.INSTANCE, value).result();
            result.ifPresent(nbtElement -> tag.put("disguise", nbtElement));
        }
    }
}
