package dev.enjarai.trickster.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.UUID;

public class DisguiseComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private UUID targetUuid = null;

    public DisguiseComponent(PlayerEntity player) {
        this.player = player;
    }

    @Nullable
    public UUID getUuid() {
        return targetUuid;
    }

    public void setUuid(@Nullable UUID targetUuid) {
        this.targetUuid = targetUuid;
        ModEntityComponents.DISGUISE.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("targetUuid")) {
            targetUuid = tag.getUuid("targetUuid");
        } else {
            targetUuid = null;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (targetUuid != null) {
            tag.putUuid("targetUuid", targetUuid);
        }
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        if (buf.readBoolean()) {
            targetUuid = buf.readUuid();
        } else {
            targetUuid = null;
        }
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(targetUuid != null);
        if (targetUuid != null) {
            buf.writeUuid(targetUuid);
        }
    }
}
