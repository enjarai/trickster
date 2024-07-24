package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.Function;

public abstract class SpellContext {
    public static final int MAX_RECURSION_DEPTH = 256;

    private final Deque<List<Fragment>> partGlyphStack = new ArrayDeque<>();
    private int recursions = 0;
    private boolean destructive = false;
    private boolean hasAffectedWorld = false;
    private final Deque<Integer> stacktrace = new ArrayDeque<>();

    protected final ManaPool manaPool;
    protected final List<ManaLink> manaLinks = new ArrayList<>();

    protected SpellContext(ManaPool manaPool, int recursions) {
        this.manaPool = manaPool;
        this.recursions = recursions;
    }

    public void pushPartGlyph(List<Fragment> fragments) throws BlunderException {
        partGlyphStack.push(fragments);
        recursions++;
        if (recursions > MAX_RECURSION_DEPTH) {
            throw new ExecutionLimitReachedBlunder();
        }
    }

    public void popPartGlyph() {
        partGlyphStack.pop();
//        recursions--; // For now, we'll actually have a maximum of 256 "function" calls in one spell execution, period.
    }

    public List<Fragment> peekPartGlyph() {
        var result = partGlyphStack.peek();
        if (result != null) {
            return result;
        }
        return List.of();
    }

    /**
     * >0: Actual index
     * -1: Glyph call
     * -2: Pattern call
     */
    public void pushStackTrace(int i) {
        stacktrace.push(i);
    }

    public void popStackTrace() {
        stacktrace.pop();
    }

    public Text formatStackTrace() {
        MutableText result = null;

        for (var i : stacktrace.reversed()) {
            if (result == null) {
                result = Text.literal("");
            } else {
                result = result.append(":");
            }

            result = result.append(switch (i) {
                case -1 -> ">";
                case -2 -> "#";
                default -> "" + i;
            });
        }

        return result == null ? Text.of("") : result;
    }

    // I am disappointed in myself for having written this.
    // Maybe I'll clean it up one day. -- Aurora D.
    public ItemStack getStack(Trick source, Optional<SlotFragment> optionalSlot, Function<Item, Boolean> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            if (!validator.apply(optionalSlot.get().getItem(source, this))) throw new ItemInvalidBlunder(source);
            result = optionalSlot.get().move(source, this);
        } else {
            var player = this.getPlayer().orElseThrow(() -> new NoPlayerBlunder(source));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (validator.apply(stack.getItem())) {
                    result = stack.copyWithCount(1);
                    stack.decrement(1);
                    break;
                }
            }
        }

        if (result == null)
            throw new MissingItemBlunder(source);

        return result;
    }

    public void addManaLink(Trick source, ManaLink link) throws BlunderException {
        for (var registeredLink : manaLinks) {
            if (registeredLink.manaPool.equals(link.manaPool)) {
                throw new EntityInvalidBlunder(source); //TODO: better exception
            }
        }

        manaLinks.add(link);
    }

    /*
     * Returns a copy of this context with recursion count reset and the provided arguments
     */
    public abstract SpellContext delayed(List<Fragment> arguments);

    public abstract void addManaLink(Trick source, LivingEntity target, float limit);

    public int getRecursions() {
        return recursions;
    }

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    public Optional<Entity> getCaster() {
        return Optional.empty();
    }

    public Optional<ItemStack> getOtherHandSpellStack() {
        return Optional.empty();
    }

    public void useMana(Trick source, float amount) throws BlunderException {
        if (!manaLinks.isEmpty()) {
            float totalAvailable = 0;
            float leftOver = 0;

            for (var link : manaLinks) {
                totalAvailable += link.getAvailable();
            }

            for (var link : manaLinks) {
                float available = link.getAvailable();
                float ratio = available / totalAvailable;
                float ratioD = amount * ratio;
                float used = link.useMana(source, ratioD);

                if (used < ratioD) {
                    leftOver += ratioD - used;
                }
            }

            amount = leftOver;
        }

        if (!manaPool.decrease(amount)) {
            throw new NotEnoughManaBlunder(source, amount);
        }
    }

    public float getMana() {
        return manaPool.get();
    }

    public float getMaxMana() {
        return manaPool.getMax();
    }

    public abstract Vector3d getPos();

    public BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public abstract ServerWorld getWorld();

    public abstract Fragment getCrowMind();

    public abstract void setCrowMind(Fragment fragment);

    public boolean isDestructive() {
        return destructive;
    }

    public SpellContext setDestructive() {
        destructive = true;
        return this;
    }

    public void setWorldAffected() {
        hasAffectedWorld = true;
    }

    public boolean hasAffectedWorld() {
        return hasAffectedWorld;
    }
}
