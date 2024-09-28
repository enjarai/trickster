package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.EntityStorageComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.screen.ScrollContainerScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class TrickHatItem extends Item implements Equipment {
    public TrickHatItem(Settings settings) {
        super(settings
                .maxCount(1)
                .component(DataComponentTypes.CONTAINER,
                        ContainerComponent.fromStacks(DefaultedList.ofSize(27, ItemStack.EMPTY)))
                .component(ModComponents.SELECTED_SLOT, new SelectedSlotComponent(0, 27))
                .component(ModComponents.ENTITY_STORAGE, new EntityStorageComponent(Optional.empty())));
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                    return stack.getName();
                } else {
                    return Text.translatable("trickster.screen.scroll_container");
                }
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScrollContainerScreenHandler(syncId, playerInventory, stack);
            }
        });

        return TypedActionResult.success(stack);
    }

    public static ItemStack getScrollRelative(ItemStack hatStack, int offset) {
        var slot = hatStack.get(ModComponents.SELECTED_SLOT);
        var container = hatStack.get(DataComponentTypes.CONTAINER);
        if (slot != null && container != null) {
            var selected = slot.slot() + offset;
            var maxSlot = (int) Math.min(slot.maxSlot(), container.stream().count());

            if (maxSlot > 0) {
                while (selected < 0) {
                    selected += maxSlot;
                }
                while (selected >= maxSlot) {
                    selected -= maxSlot;
                }
            } else {
                selected = 0;
            }

            return container.stream().skip(selected).findFirst().orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
