package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ManaCrystalItem extends Item {
    public ManaCrystalItem(Settings settings) {
        super(settings);
    }

    public static class Amethyst extends ManaCrystalItem {
        public Amethyst() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(500), false)));
        }
    }

    public static class Emerald extends ManaCrystalItem {
        public Emerald() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1000))));
        }
    }

    public static class Diamond extends ManaCrystalItem {
        public Diamond() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(2500))));
        }
    }

    public static class Echo extends ManaCrystalItem {
        public Echo() {
            super(new Settings().maxCount(2));
        }

        public ItemStack makePair() {
            var stack = getDefaultStack();
            stack.set(ModComponents.MANA, new ManaComponent(new SharedManaPool(SharedManaComponent.INSTANCE.allocate(new SimpleManaPool(4000)))));
            stack.increment(1);
            return stack;
        }
    }

    // amethyst: one-time charge, no recharge
    // emerald: boring asf, mid capacity
    // diamond: also boring asf, tho high capacity
    // echo shard: splits when ibuesddded (Aurora's hopefully clarifying comment: rai meant that the crafting recipe for a echo shard mana crystal would produce a pair which share the same mana pool)
}
