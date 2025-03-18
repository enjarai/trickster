package dev.enjarai.trickster.spell.mana;

import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.enjarai.trickster.item.component.ModComponents;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class CachedInventoryManaPool implements MutableManaPool {
    private final Inventory inventory;
    private final List<Integer> slots = new ArrayList<>();

    public CachedInventoryManaPool(Inventory inventory) {
        this.inventory = inventory;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).contains(ModComponents.MANA)) {
                slots.add(i);
            }
        }
    }

    @Override
    @Nullable
    public ManaPoolType<?> type() {
        return null;
    }

    @Override
    public float get(World world) {
        float result = 0;
        var identifiedKnots = new ArrayList<UUID>();

        for (var i : slots) {
            var comp = inventory.getStack(i).get(ModComponents.MANA);
            if (comp != null) {
                if (comp.pool() instanceof SharedManaPool(UUID uuid)) {
                    if (identifiedKnots.contains(uuid)) {
                        continue;
                    }
                    identifiedKnots.add(uuid);
                }
                result += comp.pool().get(world);
            }
        }

        return result;
    }

    @Override
    public float getMax(World world) {
        float result = 0;
        var identifiedKnots = new ArrayList<UUID>();

        for (var i : slots) {
            var comp = inventory.getStack(i).get(ModComponents.MANA);
            if (comp != null) {
                if (comp.pool() instanceof SharedManaPool(UUID uuid)) {
                    if (identifiedKnots.contains(uuid)) {
                        continue;
                    }
                    identifiedKnots.add(uuid);
                }
                result += comp.pool().getMax(world);
            }
        }

        return result;
    }

    @Override
    public void set(float value, World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMax(float value, World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float use(float amount, World world) {
        for (var i : slots) {
            var stack = inventory.getStack(i);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null)
                continue;

            var pool = comp.pool().makeClone(world);
            amount = pool.use(amount, world);
            stack.set(ModComponents.MANA, comp.with(pool));
        }

        return amount;
    }

    @Override
    public float refill(float amount, World world) {
        for (var i : slots) {
            var stack = inventory.getStack(i);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null)
                continue;

            var pool = comp.pool().makeClone(world);
            amount = pool.refill(amount, world);
            stack.set(ModComponents.MANA, comp.with(pool));
        }

        return amount;
    }

    @Override
    public MutableManaPool makeClone(World world) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
