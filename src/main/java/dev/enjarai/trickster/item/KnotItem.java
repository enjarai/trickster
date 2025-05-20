package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.TickTrackerComponent;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.TrickBlunderException;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.mana.InfiniteManaPool;
import dev.enjarai.trickster.spell.mana.SavingsManaPool;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.ToIntFunction;

public abstract class KnotItem extends Item {
    public static ToIntFunction<ItemStack> barStepFunction = i -> 0;

    private final float creationCost;

    public KnotItem(Settings settings, float creationCost) {
        super(settings.maxCount(1));
        this.creationCost = creationCost;
    }

    public static class Amethyst extends KnotItem {
        public Amethyst() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(128), 0)),
                    0);
        }
    }

    public static class Quartz extends KnotItem implements ChannelItem {
        public Quartz() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(128), 1 / 96f))
                    .component(ModComponents.TICK_CREATED, new TickTrackerComponent(0)),
                    0);
        }

        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            stack.set(ModComponents.TICK_CREATED, new TickTrackerComponent(world.getTime()));
            return stack;
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, int timeout) throws TrickBlunderException {
            return new NumberFragment(stack.get(ModComponents.TICK_CREATED).getTick(ctx.source().getWorld()));
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            if (value instanceof NumberFragment number) {
                stack.set(ModComponents.TICK_CREATED, new TickTrackerComponent(stack.get(ModComponents.TICK_CREATED).tick() - number.asInt()));
            }
        }

        public int getRange() {
            return 16;
        }
    }

    public static class Emerald extends KnotItem {
        public Emerald() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024), 1 / 12f)),
                    512);
        }
    }

    public static class Diamond extends KnotItem {
        public Diamond() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(16384), 4 / 12f)),
                    8192);
        }
    }

    public static class Echo extends KnotItem implements ChannelItem {
        public Echo() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768), 1)),
                    65536);
        }

        @Override
        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            var pool = new SimpleManaPool(32768);
            var uuid = ModGlobalComponents.SHARED_MANA.get(world.getScoreboard()).allocate(pool);
            stack.set(ModComponents.MANA, new ManaComponent(new SharedManaPool(uuid), 1));
            stack.increment(1);
            return stack;
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, int timeout) throws TrickBlunderException {
            if (stack.get(ModComponents.MANA).pool() instanceof SharedManaPool pool) {
                return new MessageListenerSpellExecutor(ctx.state(), timeout, Optional.of(new Key.Channel(pool.uuid())));
            }

            throw new ItemInvalidBlunder(trickSource);
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            if (stack.get(ModComponents.MANA).pool() instanceof SharedManaPool pool) {
                ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(new Key.Channel(pool.uuid()), value);
            }
        }

        public int getRange() {
            return 16;
        }

    }

    public static class CrackedEcho extends KnotItem {
        public CrackedEcho() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768), 2 / 12f)),
                    Float.MAX_VALUE);
        }
    }

    public static class Astral extends KnotItem {
        public Astral() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(new SavingsManaPool(1048576, (float) Math.pow(2, -20)), 0)),
                    524288);
        }
    }

    public static class Command extends KnotItem {
        public Command() {
            super(new Settings()
                    .component(ModComponents.MANA, new ManaComponent(InfiniteManaPool.INSTANCE, 0)),
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
        return barStepFunction.applyAsInt(stack);
    }

    public float getCreationCost() {
        return creationCost;
    }

    public ItemStack createStack(World world) {
        return getDefaultStack();
    }
}
