package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class SharedManaComponent implements AutoSyncedComponent {
    // this is absolutely cursed
    private static SharedManaComponent INSTANCE = null;

    private static final KeyedEndec<Map<UUID, SimpleManaPool>> POOLS_ENDEC = new KeyedEndec<>("pools", Endec.map(EndecTomfoolery.UUID, SimpleManaPool.ENDEC), new HashMap<>());

    private final Map<UUID, SimpleManaPool> pools = new HashMap<>();
    private final Map<UUID, List<UUID>> subscribers = new HashMap<>();
    private final Scoreboard provider;
    private final Optional<MinecraftServer> server;

    public SharedManaComponent(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
        this.server = Optional.ofNullable(server);
        
        // this check is very important to ensure integrated servers have the corrent object
        if (server != null || INSTANCE == null)
            INSTANCE = this;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        server.ifPresent(server -> pools.putAll(tag.get(POOLS_ENDEC)));
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        server.ifPresent(server -> tag.put(POOLS_ENDEC, pools));
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return subscribers.containsKey(player.getUuid()) && !subscribers.get(player.getUuid()).isEmpty();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity player) {
        var uuids = subscribers.get(player.getUuid());
        buf.write(POOLS_ENDEC.endec(), pools.entrySet()
                .stream()
                .filter(entry -> uuids.contains(entry.getKey()))
                .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue)));
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        pools.putAll(buf.read(POOLS_ENDEC.endec()));
    }

    public UUID allocate(SimpleManaPool pool) {
        var uuid = UUID.randomUUID();

        while (pools.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }

        pools.put(uuid, pool);
        return uuid;
    }

    public Optional<SimpleManaPool> get(UUID uuid) {
        server.ifPresent(server -> ModGlobalComponents.SHARED_MANA.sync(provider));
        return Optional.ofNullable(pools.get(uuid));
    }

    public void subscribe(ServerPlayerEntity player, UUID uuid) {
        server.ifPresent(server -> {
            var playerUuid = player.getUuid();
            var subscriptions = subscribers.get(playerUuid);

            if (subscriptions == null)
                subscriptions = new ArrayList<>();

            subscriptions.add(uuid);
            subscribers.put(playerUuid, subscriptions);
            ModGlobalComponents.SHARED_MANA.sync(provider);
        });
    }

    @Nullable
    public static SharedManaComponent getInstance() {
        return INSTANCE;
    }
}
