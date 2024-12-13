package dev.enjarai.trickster.datagen;

import java.util.Set;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModLoot {
    private static final Set<RegistryKey<LootTable>> RUSTED_SPELL_CORE_LOOT_TABLES = Set.of(
            LootTables.SIMPLE_DUNGEON_CHEST, LootTables.ABANDONED_MINESHAFT_CHEST,
            LootTables.DESERT_PYRAMID_CHEST, LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.UNDERWATER_RUIN_BIG_CHEST, LootTables.UNDERWATER_RUIN_SMALL_CHEST);
    private static final Set<RegistryKey<LootTable>> OMINOUS_SPELL_CORE_LOOT_TABLES = Set.of(
            LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST);
    private static final Set<RegistryKey<LootTable>> CRACKED_ECHO_KNOT_LOOT_TABLES = Set.of(
            LootTables.ANCIENT_CITY_CHEST);

    public static final LootFunctionType<RandomManaLootFunction> RANDOM_MANA_FUNCTION_TYPE = Registry.register(Registries.LOOT_FUNCTION_TYPE,
            Trickster.id("random_mana"),
            new LootFunctionType<>(RandomManaLootFunction.CODEC));

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (RUSTED_SPELL_CORE_LOOT_TABLES.contains(key)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.RUSTED_SPELL_CORE)
                                .conditionally(RandomChanceLootCondition.builder(0.25f)))
                        .rolls(UniformLootNumberProvider.create(0, 2)))
                        .build();
            }

            if (OMINOUS_SPELL_CORE_LOOT_TABLES.contains(key)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.OMINOUS_SPELL_CORE)
                                .conditionally(RandomChanceLootCondition.builder(0.4f)))
                        .rolls(UniformLootNumberProvider.create(0, 1)))
                        .build();
            }

            if (CRACKED_ECHO_KNOT_LOOT_TABLES.contains(key)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.CRACKED_ECHO_KNOT)
                                .conditionally(RandomChanceLootCondition.builder(0.35f))
                                .apply(() -> new RandomManaLootFunction(0.01f, 0.3f)))
                        .rolls(UniformLootNumberProvider.create(0, 1)))
                        .build();
            }
        });
    }
}
