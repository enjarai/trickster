package dev.enjarai.trickster.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class IsEditingScrollComponent implements AutoSyncedComponent {
    private final PlayerEntity player;

    private boolean editing = false;
    private boolean offhand = false;

    public IsEditingScrollComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("offhand")) {
            offhand = tag.getBoolean("offhand");
        } else {
            offhand = false;
        }

        if (tag.contains("editing")) {
            editing = tag.getBoolean("editing");
        } else {
            editing = false;
        }

        setEditing(editing, offhand);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("offhand", offhand);
        tag.putBoolean("editing", editing);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        if (buf.readBoolean()) {
            editing = buf.readBoolean();
        } else {
            editing = false;
        }
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(true);
        buf.writeBoolean(editing);
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isOffhand() {
        return offhand;
    }

    public void setEditing(boolean editing, boolean offhand) {
        this.editing = editing;
        this.offhand = offhand;
        ModEntityComponents.IS_EDITING_SCROLL.sync(player);
    }
}
