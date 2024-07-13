package dev.enjarai.trickster.effects;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEffects {
    public static final ManaBoostEffect MANA_BOOST = register("mana_boost", new ManaBoostEffect());


    private static <T extends StatusEffect> T register(String name, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, Trickster.id(name), effect);
    }

    public static void register() {
    }
}
