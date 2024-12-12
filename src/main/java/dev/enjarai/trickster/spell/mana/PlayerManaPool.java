package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.server.network.ServerPlayerEntity;
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
    public float getMax() {
        var max = 0.0f;
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) max += component.pool().getMax();
        }

        return max + super.getMax();
    }

    @Override
    public float use(float amount) {
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) {
                var pool = component.pool().makeClone();
                var left = pool.use(amount);
                slotReference.getStack().set(ModComponents.MANA, component.with(pool));
                if (left <= 0.0f) break;
            }
        }
        return super.use(amount);
    }

    @Override
    public float refill(float amount) {
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) {
                var pool = component.pool().makeClone();
                var left = pool.refill(amount);
                slotReference.getStack().set(ModComponents.MANA, component.with(pool));
                if (left <= 0.0f) break;
            }
        }
        return super.refill(amount);
    }
 
    @Override
    public float get() {
        var total = 0.0f;
        for (var slotReference : cached) {
            var component = slotReference.getStack().get(ModComponents.MANA);
            if (component != null) total += component.pool().get();
        }

        return total + super.get();
    }
}
