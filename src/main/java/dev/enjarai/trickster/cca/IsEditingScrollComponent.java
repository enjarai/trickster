package dev.enjarai.trickster.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class IsEditingScrollComponent implements AutoSyncedComponent {
    private final PlayerEntity player;

    private Boolean editing = false;

    public IsEditingScrollComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("editing")) {
            setEditing(tag.getBoolean("editing"));
        } else setEditing(false);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("editing", isEditing());
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

    public Boolean isEditing() {
        return editing;
    }

    public void setEditing(Boolean editing) {
        this.editing = editing;
        ModEntityCumponents.IS_EDITING_SCROLL.sync(player);
    }

}
