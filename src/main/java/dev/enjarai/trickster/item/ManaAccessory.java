package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.item.ItemStack;

public class ManaAccessory extends AccessoryItem {

    // max mana = 60 seconds of flight
    // takes 3 minutes to fully recharge
    // i.e 1/3 time spent flying

    private static final float recharge = 1.0f;
    private static final float maxMana = 60.0f * 20 * 3;

    public ManaAccessory() {
        super(new Settings().maxCount(1).component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(maxMana))));
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        var component = stack.get(ModComponents.MANA);
        if (component == null) return;

        var pool = component.pool().makeClone();
        pool.refill(recharge);
        stack.set(ModComponents.MANA, component.with(pool));
    }

    public ManaAccessory(Settings properties) {
        super(properties);
    }
}
