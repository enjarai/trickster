package dev.enjarai.trickster.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntity implements SlotHolderDuck {
    public ItemFrameEntityMixin(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getHeldItemStack();

    @Shadow
    public abstract void setHeldItemStack(ItemStack stack);

    @Shadow
    protected abstract void removeFromFrame(ItemStack stack);

    @Override
    public int trickster$slot_holder$size() {
        return 1;
    }

    @Override
    public ItemStack trickster$slot_holder$getStack(int slot) {
        return getHeldItemStack();
    }

    @Override
    public boolean trickster$slot_holder$setStack(int slot, ItemStack stack) {
        var currentStack = getHeldItemStack();
        setHeldItemStack(stack);
        removeFromFrame(currentStack);
        return true;
    }

    @Override
    public ItemStack trickster$slot_holder$takeFromSlot(int slot, int amount) {
        var stack = getHeldItemStack();
        setHeldItemStack(ItemStack.EMPTY);
        removeFromFrame(stack);
        return stack;
    }
}
