package dev.enjarai.trickster.advancement.criterion;

import dev.enjarai.trickster.Trickster;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCriteria {
    public static final ManaUsedCriterion MANA_USED = register("mana_used", new ManaUsedCriterion());
    public static final ManaOverfluxCriterion MANA_OVERFLUX = register("mana_overflux", new ManaOverfluxCriterion());
    public static final NaNNumberCriterion NAN_NUMBER = register("nan_number", new NaNNumberCriterion());

    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Trickster.id(name), criterion);
    }

    public static void register() {

    }
}
