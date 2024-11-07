package dev.enjarai.trickster;

import java.util.Set;

import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;

public class ModLoot {
    private static final Set<RegistryKey<LootTable>> UNSTABLE_SPELL_CORE_LOOT_TABLES = Set.of(
            LootTables.SIMPLE_DUNGEON_CHEST, LootTables.ABANDONED_MINESHAFT_CHEST, LootTables.ANCIENT_CITY_CHEST,
            LootTables.BASTION_TREASURE_CHEST, LootTables.DESERT_PYRAMID_CHEST, LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.END_CITY_TREASURE_CHEST, LootTables.STRONGHOLD_CORRIDOR_CHEST, LootTables.STRONGHOLD_CROSSING_CHEST
    );

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (UNSTABLE_SPELL_CORE_LOOT_TABLES.contains(key)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.UNSTABLE_SPELL_CORE)
                            .conditionally(RandomChanceLootCondition.builder(0.25f)))
                        .rolls(UniformLootNumberProvider.create(0, 2))).build();
            }
        });
    }
}
