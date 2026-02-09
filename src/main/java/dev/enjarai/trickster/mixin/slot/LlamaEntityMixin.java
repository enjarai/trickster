package dev.enjarai.trickster.mixin.slot;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

// Needs different logic from AbstractHorseEntity to support body armor without supporting saddle
@Mixin(LlamaEntity.class)
public abstract class LlamaEntityMixin extends AbstractDonkeyEntity implements SlotHolderDuck {
    protected LlamaEntityMixin(EntityType<? extends AbstractDonkeyEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {
            @Override
            public void clear() {
                LlamaEntityMixin.this.items.clear();
                LlamaEntityMixin.this.getInventory().clear();
            }

            @Override
            public int size() {
                return LlamaEntityMixin.this.getInventorySize();
            }

            @Override
            public boolean isEmpty() {
                return LlamaEntityMixin.this.items.isEmpty() && LlamaEntityMixin.this.getInventory().isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                if (slot == 0) {
                    return LlamaEntityMixin.this.getInventory().getStack(0);
                } else {
                    return LlamaEntityMixin.this.items.getStack(slot);
                }
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return getStack(slot).split(amount);
            }

            @Override
            public ItemStack removeStack(int slot) {
                var stack = getStack(slot);
                setStack(slot, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if (slot == 0) {
                    LlamaEntityMixin.this.getInventory().setStack(0, stack);
                } else {
                    LlamaEntityMixin.this.items.setStack(slot, stack);
                }
            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return LlamaEntityMixin.this.getInventory().canPlayerUse(player);
            }
        }, null);
    }
}
