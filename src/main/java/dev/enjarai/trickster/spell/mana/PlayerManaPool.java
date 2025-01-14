package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public class PlayerManaPool extends CachedInventoryManaPool {
    private final List<SlotReference> cached;

    public PlayerManaPool(ServerPlayerEntity player) {
        super(player.getInventory());

        var playerAccessories = player.accessoriesCapability();
        if (playerAccessories != null) {
            cached = playerAccessories.getEquipped(itemStack -> itemStack.get(ModComponents.MANA) != null).stream().map(SlotEntryReference::reference).toList();
        } else {
            cached = List.of();
        }
    }

    @Override
    public float get(World world) {
        var result = 0.0f;

        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) result += component.pool().get(world);
        }

        return result + super.get(world);
    }

    @Override
    public float getMax(World world) {
        var result = 0.0f;

        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) result += component.pool().getMax(world);
        }

        return result + super.getMax(world);
    }

    @Override
    public float use(float amount, World world) {
        for (var slotReference : cached) {
            var stack = slotReference.getStack();
            if (stack == null) continue;

            var component = stack.get(ModComponents.MANA);
            if (component == null) continue;

            var pool = component.pool().makeClone(world);
            amount = pool.use(amount, world);
            stack.set(ModComponents.MANA, component.with(pool));

            if (amount <= 0.0f) return 0;
        }

        return super.use(amount, world);
    }

    @Override
    public float refill(float amount, World world) {
        for (var slotReference : cached) {

            var stack = slotReference.getStack();
            if (stack == null) continue;

            var component = stack.get(ModComponents.MANA);
            if (component == null) continue;

            var pool = component.pool().makeClone(world);
            amount = pool.refill(amount, world);
            stack.set(ModComponents.MANA, component.with(pool));

            if (amount <= 0.0f) return 0;
        }

        return super.refill(amount, world);
    }
}
