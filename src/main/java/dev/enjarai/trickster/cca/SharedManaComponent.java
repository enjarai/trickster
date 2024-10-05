package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class SharedManaComponent implements AutoSyncedComponent {
    //TODO: this is absolutely cursed
    public static SharedManaComponent INSTANCE;

    private static final KeyedEndec<Map<UUID, SimpleManaPool>> POOLS_ENDEC = new KeyedEndec<>("pools", Endec.map(EndecTomfoolery.UUID, SimpleManaPool.ENDEC), new HashMap<>());

    private final Map<UUID, SimpleManaPool> pools = new HashMap<>();
    private final Map<UUID, List<UUID>> subscribers = new HashMap<>();
    private final Scoreboard provider;

    public SharedManaComponent(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
        INSTANCE = this;
    }

    //TODO: doesn't read any entries, but both this and writeToNbt get called... what?
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
        //TODO: add ref counting :3
        var uuid = UUID.randomUUID();

        while (pools.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }

        pools.put(uuid, pool);
        return uuid;
    }

    public Optional<SimpleManaPool> get(UUID uuid) {
        return Optional.ofNullable(pools.get(uuid));
    }

    public void subscribeInventory(ServerPlayerEntity player, Optional<? extends Inventory> otherInventory) {
        var subscriptions = new ArrayList<UUID>();

        Predicate<ItemStack> filter = stack -> {
            var comp = stack.get(ModComponents.MANA);

            if (comp != null && comp.pool() instanceof SharedManaPool shared) {
                if (!subscriptions.contains(shared.uuid()))
                    subscriptions.add(shared.uuid());
            }

            return false;
        };

        player.getInventory().containsAny(filter);
        otherInventory.ifPresent(inv -> inv.containsAny(filter));
        subscribers.put(player.getUuid(), subscriptions);
        ModGlobalComponents.SHARED_MANA.sync(provider); //TODO: needed?
    }
}
