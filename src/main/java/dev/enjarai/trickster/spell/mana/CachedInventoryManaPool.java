package dev.enjarai.trickster.spell.mana;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import dev.enjarai.trickster.item.component.ModComponents;

import java.util.List;
import java.util.ArrayList;

public class CachedInventoryManaPool implements MutableManaPool {
    private final List<ItemStack> stacks = new ArrayList<>();

    public CachedInventoryManaPool(Inventory inv) {
        inv.containsAny(stack -> {
            if (stack.contains(ModComponents.MANA)) {
                stacks.add(stack);
            }

            return false;
        });
    }

    @Override
    @Nullable
    public ManaPoolType<?> type() {
        return null;
    }

    @Override
    public float get() {
        return stacks.stream()
            .map(stack -> stack.get(ModComponents.MANA).pool().get())
            .reduce((float) 0, (f1, f2) -> f1 + f2);
    }

    @Override
    public float getMax() {
        return stacks.stream()
            .map(stack -> stack.get(ModComponents.MANA).pool().getMax())
            .reduce((float) 0, (f1, f2) -> f1 + f2);
    }

    @Override
    public void set(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMax(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float use(float amount) {
        return stacks.stream()
            .reduce(amount, (prev, stack) -> {
                if (prev > 0) {
                    var component = stack.get(ModComponents.MANA);
                    var pool = component.pool().makeClone();
                    var result = pool.use(prev);
                    stack.set(ModComponents.MANA, component.with(pool));
                    return result;
                }

                return prev;
            }, (f1, f2) -> f2);
    }

    @Override
    public float refill(float amount) {
        return stacks.stream()
            .reduce(amount, (prev, stack) -> {
                if (prev > 0) {
                    var component = stack.get(ModComponents.MANA);

                    if (component.rechargeable()) {
                        var pool = component.pool().makeClone();
                        var result = pool.refill(prev);
                        stack.set(ModComponents.MANA, component.with(pool));
                        return result;
                    }
                }

                return prev;
            }, (f1, f2) -> f2);
    }

    @Override
    public MutableManaPool makeClone() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
