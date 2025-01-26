package dev.enjarai.trickster.cca;

import java.io.File;
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
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

public class SharedManaComponent implements AutoSyncedComponent {
    private static final KeyedEndec<Map<UUID, SimpleManaPool>> POOLS_ENDEC = new KeyedEndec<>("pools", Endec.map(EndecTomfoolery.UUID, SimpleManaPool.ENDEC), new HashMap<>());

    private final Map<UUID, SimpleManaPool> pools = new HashMap<>();
    private final Map<UUID, List<UUID>> subscribers = new HashMap<>();
    private final Scoreboard provider;
    private final Optional<MinecraftServer> server;

    public SharedManaComponent(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
        this.server = Optional.ofNullable(server);
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        // We use persistent state
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        // We use persistent state
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return subscribers.containsKey(player.getUuid()) && !subscribers.get(player.getUuid()).isEmpty();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity player) {
        var uuids = subscribers.get(player.getUuid());
        buf.write(
                POOLS_ENDEC.endec(), pools.entrySet()
                        .stream()
                        .filter(entry -> uuids.contains(entry.getKey()))
                        .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue))
        );
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        pools.putAll(buf.read(POOLS_ENDEC.endec()));
    }

    public UUID allocate(SimpleManaPool pool) {
        var uuid = UUID.randomUUID();

        if (server.isPresent()) {
            final var finalPool = pool;
            pool = server.get().getOverworld().getPersistentStateManager().getOrCreate(
                    new PersistentState.Type<PoolState>(
                            () -> new PoolState(finalPool),
                            PoolState::readNbt,
                            DataFixTypes.LEVEL
                    ),
                    "trickster/shared_mana_pool/" + uuid
            ).getPool();
        }

        pools.put(uuid, pool);
        return uuid;
    }

    public Optional<SimpleManaPool> get(UUID uuid) {
        server.ifPresent(server -> {
            if (!pools.containsKey(uuid)) {
                var data = server.getOverworld().getPersistentStateManager().get(
                        PoolState.TYPE,
                        "trickster/shared_mana_pool/" + uuid
                );

                if (data != null)
                    pools.put(uuid, data.getPool());
            }
        });

        return Optional.ofNullable(pools.get(uuid));
    }

    public void subscribe(ServerPlayerEntity player, UUID uuid) {
        server.ifPresent(server -> {
            if (get(uuid).isPresent()) {
                var playerUuid = player.getUuid();
                var subscriptions = subscribers.get(playerUuid);

                if (subscriptions == null)
                    subscriptions = new ArrayList<>();

                subscriptions.add(uuid);
                subscribers.put(playerUuid, subscriptions);
                ModGlobalComponents.SHARED_MANA.sync(provider);
            }
        });
    }

    private static class PoolState extends PersistentState {
        private static final KeyedEndec<SimpleManaPool> ENDEC = new KeyedEndec<>("pool", SimpleManaPool.ENDEC, SimpleManaPool.getSingleUse(0));
        public static final Type<PoolState> TYPE = new PersistentState.Type<>(
                () -> new PoolState(SimpleManaPool.getSingleUse(0)),
                PoolState::readNbt,
                DataFixTypes.LEVEL
        );

        private final SimpleManaPool pool;

        private PoolState(SimpleManaPool pool) {
            this.pool = pool;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
            nbt.put(ENDEC, pool);
            return nbt;
        }

        public void save(File file, RegistryWrapper.WrapperLookup registryLookup) {
            file.getParentFile().mkdirs();
            setDirty(true);
            super.save(file, registryLookup);
        }

        public SimpleManaPool getPool() {
            return pool;
        }

        public static PoolState readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
            return new PoolState(nbt.get(ENDEC));
        }
    }
}
