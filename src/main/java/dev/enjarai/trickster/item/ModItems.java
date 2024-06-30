package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.lavender.book.LavenderBookItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class ModItems {
    public static final LavenderBookItem TOME_OF_TOMFOOLERY = LavenderBookItem.registerForBook(
            Trickster.id("tome_of_tomfoolery"), new Item.Settings().maxCount(1));
    public static final ScrollAndQuillItem SCROLL_AND_QUILL = register("scroll_and_quill",
            new ScrollAndQuillItem(new Item.Settings().maxCount(16)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));
    public static final EvaluationMirrorItem MIRROR_OF_EVALUATION = register("mirror_of_evaluation",
            new EvaluationMirrorItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));
    public static final TrickHatItem TOP_HAT = register("top_hat",
            new TrickHatItem(new Item.Settings().maxCount(1)
                    .component(DataComponentTypes.CONTAINER,
                            ContainerComponent.fromStacks(DefaultedList.ofSize(27, ItemStack.EMPTY)))
                    .component(ModComponents.SELECTED_SLOT, new SelectedSlotComponent(0, 27))));
    public static final WandItem WAND = register("wand",
            new WandItem(new Item.Settings().maxCount(1)
                    .component(ModComponents.SPELL, new SpellComponent(new SpellPart()))));

    public static final TagKey<Item> CAN_EVALUATE_DYNAMICALLY = TagKey.of(RegistryKeys.ITEM, Trickster.id("can_evaluate_dynamically"));
    public static final TagKey<Item> HOLDABLE_HAT = TagKey.of(RegistryKeys.ITEM, Trickster.id("holdable_hat"));
    public static final TagKey<Item> SCROLLS = TagKey.of(RegistryKeys.ITEM, Trickster.id("scrolls"));
    public static final TagKey<Item> SPELL_COST = TagKey.of(RegistryKeys.ITEM, Trickster.id("spell_cost"));
    public static final TagKey<Block> CONJURABLE_FLOWERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("conjurable_flowers"));

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(WAND::getDefaultStack)
            .displayName(Text.translatable("trickster.item_group"))
            .entries((context, entries) -> {
                entries.add(TOME_OF_TOMFOOLERY);
                entries.add(SCROLL_AND_QUILL);
                entries.add(MIRROR_OF_EVALUATION);
                entries.add(TOP_HAT);
                entries.add(WAND);
            })
            .build();

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Trickster.id(name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, Trickster.id("trickster"), ITEM_GROUP);
    }
}
