package dev.enjarai.trickster.cca;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class SharedManaComponent implements AutoSyncedComponent {
    //TODO: this is absolutely cursed
    public static SharedManaComponent INSTANCE;

    private static final KeyedEndec<Map<UUID, SimpleManaPool>> POOLS_ENDEC = new KeyedEndec<>("pools", Endec.map(EndecTomfoolery.UUID, SimpleManaPool.ENDEC), new HashMap<>());

    private final Map<UUID, SimpleManaPool> pools = new HashMap<>();
    private final Map<ServerPlayerEntity, List<UUID>> subscribers = new HashMap<>();
    private final Scoreboard provider;

    public SharedManaComponent(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
        INSTANCE = this;
    }

	@Override
	public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        pools.putAll(tag.get(POOLS_ENDEC));
	}

	@Override
	public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.put(POOLS_ENDEC, pools);
	}

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return subscribers.containsKey(player) && !subscribers.get(player).isEmpty();
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity player) {
        //TODO: hah lol
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        //TODO: my sanity is decaying
    }

    public UUID allocate(SimpleManaPool pool) {
        //TODO: add ref counting :3
        var uuid = UUID.randomUUID();

        while (pools.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }

        pools.put(uuid, pool);
        return uuid;
    }

    public SimpleManaPool get(UUID uuid) {
        return pools.get(uuid);
    }
}
