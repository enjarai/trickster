package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.item.ItemStack;

public class ManaAccessory extends AccessoryItem {
    private static final float recharge = 1.0f;
    private static final float maxMana = 256.0f;

    public ManaAccessory() {
        super(new Settings()
                .maxCount(1)
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(maxMana))));
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        var component = stack.get(ModComponents.MANA);

        if (component == null) {
            return;
        }

        var world = reference.entity().getWorld();
        var pool = component.pool().makeClone(world);

        pool.refill(recharge, world);
        stack.set(ModComponents.MANA, component.with(pool));
    }
}
