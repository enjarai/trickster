package dev.enjarai.trickster.screen;

import com.mojang.datafixers.util.Pair;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ScrollContainerScreenHandler extends ScreenHandler {
    private final SimpleInventory inventory;
    private final int rows;

    @Nullable
    private final ItemStack containerStack;

    private final SyncedProperty<Integer> lockedSlot = createProperty(Integer.class, -1);
    public final SyncedProperty<Integer> selectedSlot = createProperty(Integer.class, 0);

    public ScrollContainerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public ScrollContainerScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable ItemStack containerStack) {
        super(ModScreenHandlers.SCROLL_CONTAINER, syncId);
        this.containerStack = containerStack;
        this.rows = 3;
        this.inventory = new SimpleInventory(9 * rows);
        inventory.onOpen(playerInventory.player);

        if (containerStack != null) {
            var container = containerStack.get(DataComponentTypes.CONTAINER);
            if (container != null) {
                container.copyTo(inventory.getHeldStacks());
                inventory.addListener(i -> {
                    this.containerStack.set(DataComponentTypes.CONTAINER,
                            ContainerComponent.fromStacks(this.inventory.getHeldStacks()));
                });
            }

            var selected = containerStack.get(ModComponents.SELECTED_SLOT);
            if (selected != null) {
                selectedSlot.set(selected.slot());
            }

            for (int j = 0; j < 9; ++j) {
                if (playerInventory.getStack(j) == containerStack) {
                    lockedSlot.set(j);
                    break;
                }
            }
        }

        int i = (this.rows - 4) * 18;

        for (int j = 0; j < this.rows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new ScrollSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (int j = 0; j < 9; ++j) {
            var index = j;
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i) {
                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    return lockedSlot.get() != index;
                }
            });
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9) {
                if (!this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

    static class ScrollSlot extends Slot {
        public ScrollSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isIn(ModItems.SCROLLS);
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Trickster.id("item/empty_scroll_slot"));
        }
    }
}
