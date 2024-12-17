package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.EntityStorageComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.screen.ScrollContainerScreenHandler;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class TrickHatItem extends AccessoryItem implements Equipment {
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
        if (hand == Hand.OFF_HAND) {
            return super.use(world, user, hand);
        }

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
            var selectedSlot = slot.slot() + offset;
            var maxSlot = (int) Math.min(slot.maxSlot(), container.stream().count());

            if (maxSlot <= 1 && offset != 0) {
                return ItemStack.EMPTY;
            }

            if (maxSlot > 0) {
                while (selectedSlot < 0) {
                    selectedSlot += maxSlot;
                }
                while (selectedSlot >= maxSlot) {
                    selectedSlot -= maxSlot;
                }
            } else {
                selectedSlot = 0;
            }

            return container.stream()
                    .skip(selectedSlot)
                    .findFirst()
                    .orElse(ItemStack.EMPTY);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
        return false;
    }
}
