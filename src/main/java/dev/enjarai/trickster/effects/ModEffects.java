package dev.enjarai.trickster.effects;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> MANA_BOOST = register("mana_boost", new ManaBoostEffect());
    public static final RegistryEntry<StatusEffect> MANA_DEFICIENCY = register("mana_deficiency", new ManaDeficiencyEffect());


    private static <T extends StatusEffect> RegistryEntry<StatusEffect> register(String name, T effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Trickster.id(name), effect);
    }

    public static void register() {
    }
}
