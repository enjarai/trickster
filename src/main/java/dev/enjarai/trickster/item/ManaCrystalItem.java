package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class ManaCrystalItem extends Item {
    public static BiConsumer<ItemStack, List<Text>> merlinTooltipAppender;

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
            var stack = getDefaultStack();
            var pool = new SimpleManaPool(32768);
            var uuid = SharedManaComponent.getInstance().allocate(pool);
            stack.set(ModComponents.MANA, new ManaComponent(new SharedManaPool(uuid), 1));
            stack.increment(1);
            return stack;
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

        // if ever run on the server, will fail -- consider putting a try-catch if it causes an issue with a mod?
        if (manaComponent.pool() instanceof SharedManaPool sharedPool && SharedManaComponent.getInstance().get(sharedPool.uuid()).isEmpty()) {
            ModNetworking.CHANNEL.clientHandle().send(new SubscribeToPoolPacket(sharedPool.uuid()));
        }

        float poolMax = manaComponent.pool().getMax();
        return poolMax == 0 ? 0 : MathHelper.clamp(Math.round(manaComponent.pool().get() * 13.0F / poolMax), 0, 13);
    }

    public float getCreationCost() {
        return creationCost;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (merlinTooltipAppender != null) {
            merlinTooltipAppender.accept(stack, tooltip);
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    public ItemStack createStack() {
        return getDefaultStack();
    }
}
