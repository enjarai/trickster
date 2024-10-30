package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class ManaCrystalItem extends Item {
    private final float creationCost;

    public ManaCrystalItem(Settings settings, float creationCost) {
        super(settings.maxCount(1));
        this.creationCost = creationCost;
    }

    public static class Amethyst extends ManaCrystalItem {
        public Amethyst() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(128), 0, false)),
                    0);
        }
    }

    public static class Emerald extends ManaCrystalItem {
        public Emerald() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024), 1 / 12f)),
                    512);
        }
    }

    public static class Diamond extends ManaCrystalItem {
        public Diamond() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(16384), 4 / 12f)),
                    8192);
        }
    }

    public static class Echo extends ManaCrystalItem {
        public Echo() {
            super(new Settings(), 65536);
        }

        @Override
        public ItemStack createStack() {
            throw new UnsupportedOperationException();
        }
    }

    // amethyst: one-time charge, no recharge
    // emerald: boring asf, mid capacity
    // diamond: also boring asf, tho high capacity
    // echo shard: splits when ibuesddded (Aurora's hopefully clarifying comment: rai meant that the crafting recipe for a echo shard mana crystal would produce a pair which share the same mana pool)

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.contains(ModComponents.MANA);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xbb99ff;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var manaComponent = stack.get(ModComponents.MANA);
        if (manaComponent == null) {
            return 0;
        }

        float poolMax = manaComponent.pool().getMax();
        return poolMax == 0 ? 0 : MathHelper.clamp(Math.round(manaComponent.pool().get() * 13.0F / poolMax), 0, 13);
    }

    public float getCreationCost() {
        return creationCost;
    }

    public ItemStack createStack() {
        return getDefaultStack();
    }
}
