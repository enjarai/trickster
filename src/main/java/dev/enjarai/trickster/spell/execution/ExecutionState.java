package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.ManaLink;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.trick.blunder.ExecutionLimitReachedBlunder;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class ExecutionState {
    public static final int MAX_RECURSION_DEPTH = 255;
    public static final Codec<ExecutionState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("recursions").forGetter(ExecutionState::getRecursions),
            Codec.INT.fieldOf("delay").forGetter(ExecutionState::getDelay),
            Codec.BOOL.fieldOf("has_used_mana").forGetter(ExecutionState::hasUsedMana),
            Fragment.CODEC.get().codec().listOf().fieldOf("arguments").forGetter(state -> state.arguments),
            Codec.INT.listOf().fieldOf("stacktrace").forGetter(state -> state.stacktrace.stream().toList()),
            ManaLink.CODEC.listOf().fieldOf("mana_links").forGetter(state -> state.manaLinks),
            ManaPool.CODEC.get().codec().optionalFieldOf("pool_override").forGetter(state -> state.poolOverride)
    ).apply(instance, ExecutionState::new));

    protected int recursions;
    protected int delay;
    private boolean hasUsedMana = false;
    private final List<Fragment> arguments;
    private final Deque<Integer> stacktrace = new ArrayDeque<>();
    private final List<ManaLink> manaLinks = new ArrayList<>();
    private final Optional<ManaPool> poolOverride;

    private ExecutionState(int recursions, int delay, boolean hasUsedMana, List<Fragment> arguments, List<Integer> stacktrace, List<ManaLink> manaLinks, Optional<ManaPool> poolOverride) {
        this.recursions = recursions;
        this.delay = delay;
        this.hasUsedMana = hasUsedMana;
        this.arguments = arguments;
        this.stacktrace.addAll(stacktrace);
        this.manaLinks.addAll(manaLinks);
        this.poolOverride = poolOverride;
    }

    public ExecutionState(List<Fragment> arguments) {
        this(0, 0, false, arguments, List.of(), List.of(), Optional.empty());
    }

    public ExecutionState(List<Fragment> arguments, ManaPool poolOverride) {
        this(0, 0, false, arguments, List.of(), List.of(), Optional.ofNullable(poolOverride));
    }

    private ExecutionState(int recursions, List<Fragment> arguments, Optional<ManaPool> poolOverride) {
        this(recursions, 0, false, arguments, List.of(), List.of(), poolOverride);
    }

    public ExecutionState recurseOrThrow(List<Fragment> arguments) throws ExecutionLimitReachedBlunder {
        if (recursions + 1 >= MAX_RECURSION_DEPTH) {
            throw new ExecutionLimitReachedBlunder();
        }

        var state = new ExecutionState(recursions + 1, arguments, poolOverride);
        state.stacktrace.addAll(stacktrace);
        state.stacktrace.push(-2);
        return state;
    }

    public void decrementRecursions() {
        recursions--;
        // Remove the function call instruction that was added by recursing from the stacktrace,
        // and add a tail recursion one instead.
        stacktrace.pop();
        if (stacktrace.isEmpty() || stacktrace.peek() != -3) {
            stacktrace.push(-3);
        }
    }

    public ManaPool tryOverridePool(ManaPool pool) {
        return poolOverride.orElse(pool);
    }

    public List<Fragment> getArguments() {
        return arguments;
    }

    public int getRecursions() {
        return recursions;
    }

    public boolean isDelayed() {
        return delay > 0;
    }

    public void addDelay(int ticks) {
        delay += ticks;
    }

    public void decrementDelay() {
        delay--;
    }

    public int getDelay() {
        return delay;
    }

    /**
     * >0: Actual index
     * -1: Glyph call
     * -2: Pattern call (Temporarily unused)
     * -3: Tail recursion
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
                case -3 -> "&";
                default -> "" + i;
            });
        }

        return result == null ? Text.of("") : result;
    }

    public Deque<Integer> getStacktrace() {
        return stacktrace;
    }

    public void addManaLink(Trick trickSource, LivingEntity target, float ownerHealth, float limit) throws BlunderException {
        addManaLink(trickSource, new ManaLink(target, ownerHealth, limit));
    }

    public void addManaLink(Trick source, ManaLink link) throws EntityInvalidBlunder {
        for (var registeredLink : manaLinks) {
            if (registeredLink.manaPool.equals(link.manaPool)) {
                throw new EntityInvalidBlunder(source); //TODO: better exception
            }
        }

        manaLinks.add(link);
    }

    public boolean hasUsedMana() {
        return hasUsedMana;
    }

    public void useMana(Trick trickSource, SpellContext ctx, ManaPool pool, float amount) throws NotEnoughManaBlunder {
        hasUsedMana = true;

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
                float used = link.useMana(trickSource, ctx.source().getWorld(), tryOverridePool(ctx.source().getManaPool()), ratioD);

                if (used < ratioD) {
                    leftOver += ratioD - used;
                }
            }

            amount = leftOver;
        }

        if (!pool.decrease(amount)) {
            throw new NotEnoughManaBlunder(trickSource, amount);
        }
    }
}
