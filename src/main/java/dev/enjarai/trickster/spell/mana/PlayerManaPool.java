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
    public float getMax(World world) {
        var max = 0.0f;
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) max += component.pool().getMax(world);
        }

        return max + super.getMax(world);
    }

    @Override
    public float use(float amount, World world) {
        for (var slotReference : cached) {
            var stack = slotReference.getStack();
            if (stack == null)  continue;

            var component = stack.get(ModComponents.MANA);
            if (component == null) continue;

            var pool = component.pool().makeClone(world);
            amount = pool.use(amount, world);
            stack.set(ModComponents.MANA, component.with(pool));

            if (amount <= 0.0f) break;
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

            if (amount <= 0.0f) break;
        }
        return super.refill(amount, world);
    }
 
    @Override
    public float get(World world) {
        var total = 0.0f;
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) total += component.pool().get(world);
        }

        return total + super.get(world);
    }
}
