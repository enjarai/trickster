package dev.enjarai.trickster.advancement.criterion;

import dev.enjarai.trickster.Trickster;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCriteria {
    public static final ManaUsedCriterion MANA_USED = register("mana_used", new ManaUsedCriterion());
    public static final ManaOverfluxCriterion MANA_OVERFLUX = register("mana_overflux", new ManaOverfluxCriterion());
    public static final CreateEchoKnotCriterion NAN_NUMBER = register("nan_number", new CreateEchoKnotCriterion());
    public static final DrinkSpellInkCriterion DRINK_SPELL = register("drink_spell_ink", new DrinkSpellInkCriterion());
    public static final TriggerResonatorCriterion TRIGGER_RESONATOR = register("trigger_resonator", new TriggerResonatorCriterion());
    public static final InscribeSpellCriterion INSCRIBE_SPELL = register("inscribe_spell", new InscribeSpellCriterion());
    public static final UseMacroCriterion USE_MACRO = register("use_macro", new UseMacroCriterion());
    public static final TriggerWardCriterion TRIGGER_WARD = register("trigger_ward", new TriggerWardCriterion());
    public static final CreateEchoKnotCriterion CREATE_ECHO_KNOT = register("create_echo_knot", new CreateEchoKnotCriterion());
    public static final UseCostPloyCriterion USE_COST_PLOY = register("use_cost_ploy", new UseCostPloyCriterion());

    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Trickster.id(name), criterion);
    }

    public static void register() {

    }
}
