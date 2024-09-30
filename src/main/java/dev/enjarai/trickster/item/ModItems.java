package dev.enjarai.trickster.item;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.item.component.*;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import io.wispforest.lavender.book.LavenderBookItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
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
import java.util.Optional;

public class ModItems {
    public static final LavenderBookItem TOME_OF_TOMFOOLERY = LavenderBookItem.registerForBook(
            Trickster.id("tome_of_tomfoolery"), new Item.Settings().maxCount(1));
    public static final WrittenScrollItem WRITTEN_SCROLL = register("written_scroll",
            new WrittenScrollItem(new Item.Settings().maxCount(16)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart(), true))
                    .component(ModComponents.WRITTEN_SCROLL_META,
                            new WrittenScrollMetaComponent("Unknown", "Unknown", 0))));
    public static final ScrollAndQuillItem SCROLL_AND_QUILL = register("scroll_and_quill",
            new ScrollAndQuillItem(new Item.Settings().maxCount(16)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart())), WRITTEN_SCROLL));
    public static final EvaluationMirrorItem MIRROR_OF_EVALUATION = register("mirror_of_evaluation",
            new EvaluationMirrorItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));
    public static final TrickHatItem TOP_HAT = register("top_hat",
            new TrickHatItem(new Item.Settings()));
    public static final TrickHatItem WITCH_HAT = register("witch_hat",
            new TrickHatItem(new Item.Settings()));
    public static final TrickHatItem FEZ = register("fez",
            new TrickHatItem(new Item.Settings()));
    public static final WandItem WAND = register("wand",
            new WandItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));
    public static final Item MACRO_RING = register("macro_ring",
            new Item(new Item.Settings().maxCount(1)
                    .component(ModComponents.MACRO_MAP, new MacroComponent(Hamt.empty()))));
    public static final TrickyAccessoryItem WARDING_CHARM = register("warding_charm",
            new TrickyAccessoryItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));
    public static final SpellInkItem SPELL_INK = register("spell_ink",
            new SpellInkItem(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE)
                    .food(new FoodComponent(0,
                            0,
                            true,
                            1,
                            Optional.of(Items.GLASS_BOTTLE.getDefaultStack()),
                            List.of(new FoodComponent.StatusEffectEntry(new StatusEffectInstance(StatusEffects.NAUSEA, 60 * 20), 1),
                                    new FoodComponent.StatusEffectEntry(new StatusEffectInstance(StatusEffects.POISON, 60 * 20), 1),
                                    new FoodComponent.StatusEffectEntry(new StatusEffectInstance(StatusEffects.GLOWING, 60 * 20), 1),
                                    new FoodComponent.StatusEffectEntry(new StatusEffectInstance(StatusEffects.BLINDNESS, 60 * 20), 1))))));
    public static final BlockItem SPELL_RESONATOR_BLOCK_ITEM = register("spell_resonator", new BlockItem(ModBlocks.SPELL_RESONATOR, new Item.Settings()));
    public static final BlockItem SCROLL_SHELF_BLOCK_ITEM = register("scroll_shelf", new BlockItem(ModBlocks.SCROLL_SHELF, new Item.Settings()));

    public static final TagKey<Item> CAN_EVALUATE_DYNAMICALLY = TagKey.of(RegistryKeys.ITEM, Trickster.id("can_evaluate_dynamically"));
    public static final TagKey<Item> HOLDABLE_HAT = TagKey.of(RegistryKeys.ITEM, Trickster.id("holdable_hat"));
    public static final TagKey<Item> SCROLLS = TagKey.of(RegistryKeys.ITEM, Trickster.id("scrolls"));
    //    public static final TagKey<Item> IMMUTABLE_SPELL_HOLDERS = TagKey.of(RegistryKeys.ITEM, Trickster.id("immutable_spell_holders"));
    public static final TagKey<Item> SPELL_COST = TagKey.of(RegistryKeys.ITEM, Trickster.id("spell_cost"));
    public static final TagKey<Item> NO_SPELL_GLINT = TagKey.of(RegistryKeys.ITEM, Trickster.id("no_spell_glint"));
    public static final TagKey<Item> WEAPON_SPELL_TRIGGERS = TagKey.of(RegistryKeys.ITEM, Trickster.id("weapon_spell_triggers"));
    public static final TagKey<Block> CONJURABLE_FLOWERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("conjurable_flowers"));

    public static final WrittenScrollItem[] COLORED_WRITTEN_SCROLLS = new WrittenScrollItem[DyeColor.values().length];
    public static final ScrollAndQuillItem[] COLORED_SCROLLS_AND_QUILLS = new ScrollAndQuillItem[DyeColor.values().length];
    public static final List<DyedVariant> DYED_VARIANTS;

    static {
        var list = ImmutableList.<DyedVariant>builder();
        for (int i = 0; i < DyeColor.values().length; i++) {
            var color = DyeColor.values()[i];

            var writtenScroll = register("written_scroll_" + color.getName(),
                    new WrittenScrollItem(new Item.Settings().maxCount(16)
                            .component(ModComponents.SPELL, new SpellComponent(new SpellPart(), true))
                            .component(ModComponents.WRITTEN_SCROLL_META,
                                    new WrittenScrollMetaComponent("Unknown", "Unknown", 0))));
            list.add(new DyedVariant(WRITTEN_SCROLL, writtenScroll, color));
            COLORED_WRITTEN_SCROLLS[i] = writtenScroll;

            var scrollAndQuill = register("scroll_and_quill_" + color.getName(),
                    new ScrollAndQuillItem(new Item.Settings().maxCount(16)
                            .component(ModComponents.SPELL, new SpellComponent(new SpellPart())), writtenScroll));
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
                entries.add(WARDING_CHARM);
                entries.add(MACRO_RING);
                entries.add(SPELL_INK);
                entries.add(SPELL_RESONATOR_BLOCK_ITEM);
                entries.add(SCROLL_SHELF_BLOCK_ITEM);
            })
            .build();

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Trickster.id(name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, Trickster.id("trickster"), ITEM_GROUP);
    }

    public record DyedVariant(Item original, Item variant, DyeColor color) {}
}
