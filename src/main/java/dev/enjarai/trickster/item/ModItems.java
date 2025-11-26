package dev.enjarai.trickster.item;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.item.component.*;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.effects.ModEffects;
import io.wispforest.lavender.book.LavenderBookItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.Arrays;
import java.util.List;

public class ModItems {
    public static final LavenderBookItem TOME_OF_TOMFOOLERY = LavenderBookItem.registerForBook(
            Trickster.id("tome_of_tomfoolery"), new Item.Settings().maxCount(1));
    public static final WrittenScrollItem WRITTEN_SCROLL = register("written_scroll",
            new WrittenScrollItem(new Item.Settings().maxCount(16)
                    .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart(), true))
                    .component(ModComponents.WRITTEN_SCROLL_META,
                            new WrittenScrollMetaComponent("Unknown", "Unknown", 0))));
    public static final ScrollAndQuillItem SCROLL_AND_QUILL = register("scroll_and_quill",
            new ScrollAndQuillItem(new Item.Settings().maxCount(16)
                    .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart())), WRITTEN_SCROLL));
    public static final EvaluationMirrorItem MIRROR_OF_EVALUATION = register("mirror_of_evaluation",
            new EvaluationMirrorItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart()))));
    public static final TrickHatItem TOP_HAT = register("top_hat",
            new TrickHatItem(new Item.Settings()));
    public static final TrickHatItem WITCH_HAT = register("witch_hat",
            new TrickHatItem(new Item.Settings()));
    public static final TrickHatItem FEZ = register("fez",
            new TrickHatItem(new Item.Settings()));
    public static final WandItem WAND = register("wand",
            new WandItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart()))));
    public static final Item MACRO_RING = register("macro_ring",
            new Item(new Item.Settings().maxCount(1)));
    public static final ManaAccessory AMETHYST_WHORL = register("amethyst_whorl", new ManaAccessory());
    public static final SpellAccessory TORMENT_ON_A_CHAIN = register("torment_on_a_chain", new SpellAccessory());
    public static final CollarItem COLLAR = register("collar", new CollarItem(new Item.Settings()));
    public static final LeashItem LEASH = register("leash", new LeashItem(new Item.Settings()));
    public static final Item WARDING_CHARM = register("warding_charm",
            new Item(new Item.Settings().maxCount(1)));
    public static final SpellInkItem SPELL_INK = register("spell_ink",
            new SpellInkItem(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE)
                    .food(new FoodComponent.Builder()
                            .nutrition(0)
                            .saturationModifier(0)
                            .alwaysEdible()
                            .usingConvertsTo(Items.GLASS_BOTTLE)
                            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60 * 20), 1)
                            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * 20), 1)
                            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60 * 20), 1)
                            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60 * 20), 1)
                            .statusEffect(new StatusEffectInstance(ModEffects.MANA_BOOST, 60 * 20), 1)
                            .build())));
    public static final SpellCoreItem SPELL_CORE = register("spell_core", new SpellCoreItem());
    public static final RustedSpellCoreItem RUSTED_SPELL_CORE = register("rusted_spell_core", new RustedSpellCoreItem());
    public static final OminousSpellCoreItem OMINOUS_SPELL_CORE = register("ominous_spell_core", new OminousSpellCoreItem());
    public static final InactiveSpawnerSpellCoreItem INACTIVE_SPAWNER_SPELL_CORE = register("inactive_spawner_spell_core", new InactiveSpawnerSpellCoreItem());
    public static final SpawnerSpellCoreItem SPAWNER_SPELL_CORE = register("spawner_spell_core", new SpawnerSpellCoreItem());
    public static final BlockItem SPELL_RESONATOR_ITEM = register("spell_resonator", new BlockItem(ModBlocks.SPELL_RESONATOR, new Item.Settings()));
    public static final BlockItem SPELL_CONSTRUCT_ITEM = register("spell_construct", new BlockItem(ModBlocks.SPELL_CONSTRUCT, new Item.Settings()));
    public static final BlockItem MODULAR_SPELL_CONSTRUCT_ITEM = register("modular_spell_construct", new BlockItem(ModBlocks.MODULAR_SPELL_CONSTRUCT, new Item.Settings()));
    public static final BlockItem SCROLL_SHELF_BLOCK_ITEM = register("scroll_shelf", new BlockItem(ModBlocks.SCROLL_SHELF, new Item.Settings()));
    public static final BlockItem CHARGING_ARRAY_ITEM = register("charging_array", new BlockItem(ModBlocks.CHARGING_ARRAY, new Item.Settings()));
    public static final BlockItem INERT_SPAWNER_ITEM = register("inert_spawner", new BlockItem(ModBlocks.INERT_SPAWNER, new Item.Settings()));
    public static final KnotItem.Amethyst AMETHYST_KNOT = register("amethyst_knot", new KnotItem.Amethyst());
    public static final KnotItem.CrackedAmethyst CRACKED_AMETHYST_KNOT = register("cracked_amethyst_knot", new KnotItem.CrackedAmethyst());
    public static final KnotItem.Quartz QUARTZ_KNOT = register("quartz_knot", new KnotItem.Quartz());
    public static final KnotItem.CrackedQuartz CRACKED_QUARTZ_KNOT = register("cracked_quartz_knot", new KnotItem.CrackedQuartz());
    public static final KnotItem.Emerald EMERALD_KNOT = register("emerald_knot", new KnotItem.Emerald());
    public static final KnotItem.CrackedEmerald CRACKED_EMERALD_KNOT = register("cracked_emerald_knot", new KnotItem.CrackedEmerald());
    public static final KnotItem.Diamond DIAMOND_KNOT = register("diamond_knot", new KnotItem.Diamond());
    public static final KnotItem.CrackedDiamond CRACKED_DIAMOND_KNOT = register("cracked_diamond_knot", new KnotItem.CrackedDiamond());
    public static final KnotItem.Echo ECHO_KNOT = register("echo_knot", new KnotItem.Echo());
    public static final KnotItem.CrackedEcho CRACKED_ECHO_KNOT = register("cracked_echo_knot", new KnotItem.CrackedEcho());
    public static final KnotItem.Astral ASTRAL_KNOT = register("astral_knot", new KnotItem.Astral());
    public static final KnotItem.CrackedAstral CRACKED_ASTRAL_KNOT = register("cracked_astral_knot", new KnotItem.CrackedAstral());
    public static final KnotItem.Command COMMAND_KNOT = register("command_knot", new KnotItem.Command());

    // Advancement icon
    public static final Item NAN = register("nan", new Item(new Item.Settings()));

    public static final TagKey<Item> CAN_EVALUATE_DYNAMICALLY = TagKey.of(RegistryKeys.ITEM, Trickster.id("can_evaluate_dynamically"));
    public static final TagKey<Item> HOLDABLE_HAT = TagKey.of(RegistryKeys.ITEM, Trickster.id("holdable_hat"));
    public static final TagKey<Item> SCROLLS = TagKey.of(RegistryKeys.ITEM, Trickster.id("scrolls"));
    public static final TagKey<Item> SCROLL_AND_QUILLS = TagKey.of(RegistryKeys.ITEM, Trickster.id("scroll_and_quills"));
    public static final TagKey<Item> SPELL_COST = TagKey.of(RegistryKeys.ITEM, Trickster.id("spell_cost"));
    public static final TagKey<Item> NO_SPELL_GLINT = TagKey.of(RegistryKeys.ITEM, Trickster.id("no_spell_glint"));
    public static final TagKey<Item> WEAPON_SPELL_TRIGGERS = TagKey.of(RegistryKeys.ITEM, Trickster.id("weapon_spell_triggers"));
    public static final TagKey<Item> MANA_KNOTS = TagKey.of(RegistryKeys.ITEM, Trickster.id("mana_knots"));
    public static final TagKey<Item> CANT_DESPAWN = TagKey.of(RegistryKeys.ITEM, Trickster.id("cant_despawn"));
    public static final TagKey<Item> COLLARS = TagKey.of(RegistryKeys.ITEM, Trickster.id("collars"));
    public static final TagKey<Item> TICK_TOCK = TagKey.of(RegistryKeys.ITEM, Trickster.id("tick_tock"));
    public static final TagKey<Item> LEFT_CLICK_USE = TagKey.of(RegistryKeys.ITEM, Trickster.id("left_click_use"));

    public static final WrittenScrollItem[] COLORED_WRITTEN_SCROLLS = new WrittenScrollItem[DyeColor.values().length];
    public static final ScrollAndQuillItem[] COLORED_SCROLLS_AND_QUILLS = new ScrollAndQuillItem[DyeColor.values().length];
    public static final List<DyedVariant> DYED_VARIANTS;

    static {
        var list = ImmutableList.<DyedVariant>builder();
        for (int i = 0; i < DyeColor.values().length; i++) {
            var color = DyeColor.values()[i];

            var writtenScroll = register("written_scroll_" + color.getName(),
                    new WrittenScrollItem(new Item.Settings().maxCount(16)
                            .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart(), true))
                            .component(ModComponents.WRITTEN_SCROLL_META,
                                    new WrittenScrollMetaComponent("Unknown", "Unknown", 0))));
            list.add(new DyedVariant(WRITTEN_SCROLL, writtenScroll, color));
            COLORED_WRITTEN_SCROLLS[i] = writtenScroll;

            var scrollAndQuill = register("scroll_and_quill_" + color.getName(),
                    new ScrollAndQuillItem(new Item.Settings().maxCount(16)
                            .component(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart())), writtenScroll));
            list.add(new DyedVariant(SCROLL_AND_QUILL, scrollAndQuill, color));
            COLORED_SCROLLS_AND_QUILLS[i] = scrollAndQuill;
        }
        DYED_VARIANTS = list.build();
    }

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(WAND::getDefaultStack)
            .displayName(Text.translatable("trickster.item_group"))
            .entries((context, entries) -> {
                entries.add(TOME_OF_TOMFOOLERY);
                entries.add(SCROLL_AND_QUILL);
                entries.addAll(Arrays.stream(COLORED_SCROLLS_AND_QUILLS).map(ItemStack::new).toList());
                entries.add(MIRROR_OF_EVALUATION);
                entries.add(TOP_HAT);
                entries.add(WITCH_HAT);
                entries.add(FEZ);
                entries.add(WAND);
                entries.add(AMETHYST_WHORL);
                entries.add(TORMENT_ON_A_CHAIN);
                entries.add(COLLAR);
                entries.add(LEASH);
                entries.add(WARDING_CHARM);
                entries.add(MACRO_RING);
                entries.add(SPELL_INK);
                entries.add(SPELL_RESONATOR_ITEM);
                entries.add(SCROLL_SHELF_BLOCK_ITEM);
                entries.add(SPELL_CONSTRUCT_ITEM);
                entries.add(MODULAR_SPELL_CONSTRUCT_ITEM);
                entries.add(CHARGING_ARRAY_ITEM);
                entries.add(INERT_SPAWNER_ITEM);
                entries.add(SPELL_CORE);
                entries.add(RUSTED_SPELL_CORE);
                entries.add(OMINOUS_SPELL_CORE);
                entries.add(INACTIVE_SPAWNER_SPELL_CORE);
                entries.add(SPAWNER_SPELL_CORE);
                entries.add(AMETHYST_KNOT);
                entries.add(CRACKED_AMETHYST_KNOT);
                entries.add(QUARTZ_KNOT);
                entries.add(CRACKED_QUARTZ_KNOT);
                entries.add(EMERALD_KNOT);
                entries.add(CRACKED_EMERALD_KNOT);
                entries.add(DIAMOND_KNOT);
                entries.add(CRACKED_DIAMOND_KNOT);
                entries.add(ECHO_KNOT);
                entries.add(CRACKED_ECHO_KNOT);
                entries.add(ASTRAL_KNOT);
                entries.add(CRACKED_ASTRAL_KNOT);
                entries.add(COMMAND_KNOT);
            })
            .build();

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Trickster.id(name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, Trickster.id("trickster"), ITEM_GROUP);
    }

    public record DyedVariant(Item original, Item variant, DyeColor color) {
    }
}
