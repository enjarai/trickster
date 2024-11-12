package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.util.ClientUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public abstract class KnotItem extends Item {
    private final float creationCost;

    public KnotItem(Settings settings, float creationCost) {
        super(settings);
        this.creationCost = creationCost;
    }

    public static class Amethyst extends KnotItem {
        public Amethyst() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(128), 0, false)),
                    0);
        }
    }

    public static class Emerald extends KnotItem {
        public Emerald() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024), 1 / 12f)),
                    512);
        }
    }

    public static class Diamond extends KnotItem {
        public Diamond() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(16384), 4 / 12f)),
                    8192);
        }
    }

    public static class Echo extends KnotItem {
        public Echo() {
            super(new Settings()
                    .maxCount(2)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768), 1)),
                    65536);
        }

        @Override
        public ItemStack createStack() {
            var stack = getDefaultStack();
            var pool = new SimpleManaPool(32768);
            var uuid = SharedManaComponent.getInstance().allocate(pool);
            stack.set(ModComponents.MANA, new ManaComponent(new SharedManaPool(uuid), 1));
            stack.increment(1);
            return stack;
        }
    }

    public static class CrackedEcho extends KnotItem {
        public CrackedEcho() {
            super(new Settings()
                    .maxCount(1)
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768), 2 / 12f)),
                    Float.MAX_VALUE);
        }
    }

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

        ClientUtils.trySubscribe(manaComponent);

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
