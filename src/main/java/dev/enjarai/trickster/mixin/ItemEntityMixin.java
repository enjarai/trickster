package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.pond.SlotHolderDuck;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements SlotHolderDuck {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void chargeCrystal(CallbackInfo ci) {
        if (getWorld() instanceof ServerWorld world)
            ManaComponent.tryRecharge(world, getPos(), getStack());
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V", ordinal = 1))
    private boolean cancelDespawn(ItemEntity instance) {
        if (getStack().isIn(ModItems.CANT_DESPAWN))
            return false;

        if (getStack().get(ModComponents.MANA) instanceof ManaComponent mana && mana.naturalRechargeMultiplier() != 0)
            return false;

        return true;
    }

    @Override
    public int trickster$slot_holder$size() {
        return 1;
    }

    @Override
    public ItemStack trickster$slot_holder$getStack(int slot) {
        return getStack();
    }

    @Override
    public boolean trickster$slot_holder$setStack(int slot, ItemStack stack) {
        setStack(stack);
        return true;
    }

    @Override
    public ItemStack trickster$slot_holder$takeFromSlot(int slot, int amount) {
        var stack = getStack().copyWithCount(amount);
        getStack().decrement(amount);
        return stack;
    }
}
