package dev.enjarai.trickster.advancement.criterion;

import dev.enjarai.trickster.Trickster;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCriteria {
    public static final ManaUsedCriterion MANA_USED = register("mana_used", new ManaUsedCriterion());
    public static final ManaOverfluxCriterion MANA_OVERFLUX = register("mana_overflux", new ManaOverfluxCriterion());
    public static final NaNNumberCriterion NAN_NUMBER = register("nan_number", new NaNNumberCriterion());
    public static final DrinkSpellInkCriterion DRINK_SPELL = register("drink_spell_ink", new DrinkSpellInkCriterion());
    public static final TriggerResonatorCriterion TRIGGER_RESONATOR = register("trigger_resonator", new TriggerResonatorCriterion());
    public static final InscribeSpellCriterion INSCRIBE_SPELL = register("inscribe_spell", new InscribeSpellCriterion());
    public static final UseMacroCriterion USE_MACRO = register("use_macro", new UseMacroCriterion());
    public static final TriggerWardCriterion TRIGGER_WARD = register("trigger_ward", new TriggerWardCriterion());
    public static final CreateKnotCriterion CREATE_KNOT = register("create_knot", new CreateKnotCriterion());
    public static final UseCostPloyCriterion USE_COST_PLOY = register("use_cost_ploy", new UseCostPloyCriterion());
    public static final CrackKnotCriterion CRACK_KNOT = register("crack_knot", new CrackKnotCriterion());
    public static final DestroyKnotCriterion DESTROY_KNOT = register("destroy_knot", new DestroyKnotCriterion());

    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Trickster.id(name), criterion);
    }

    public static void register() {

    }
}
