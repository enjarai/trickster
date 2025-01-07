package dev.enjarai.trickster.spell.execution;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.ExecutionLimitReachedBlunder;
import dev.enjarai.trickster.spell.blunder.NotEnoughManaBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class ExecutionState {
    public static final int MAX_RECURSION_DEPTH = 255;
    public static final StructEndec<ExecutionState> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("recursions", ExecutionState::getRecursions),
            Endec.INT.fieldOf("delay", ExecutionState::getDelay),
            Endec.BOOLEAN.fieldOf("has_used_mana", ExecutionState::hasUsedMana),
            Endec.INT.fieldOf("stacktrace_size_when_made", ExecutionState::getInitialStacktraceSize),
            Fragment.ENDEC.listOf().fieldOf("arguments", state -> state.arguments),
            Endec.map(Pattern.ENDEC, SpellPart.ENDEC).fieldOf("imported_tricks", state -> state.importedTricks),
            Endec.INT.listOf().fieldOf("stacktrace", state -> state.stacktrace.stream().toList()),
            EndecTomfoolery.safeOptionalOf(MutableManaPool.ENDEC).optionalFieldOf("pool_override", state -> state.poolOverride, Optional.empty()),
            ExecutionState::new
    );

    private int recursions;
    private int delay;
    private boolean hasUsedMana;
    private final int initialStacktraceSize;
    private final List<Fragment> arguments;
    private final Deque<Integer> stacktrace = new ArrayDeque<>();
    private final Optional<MutableManaPool> poolOverride;
    private final Map<Pattern, SpellPart> importedTricks;

    private ExecutionState(int recursions, int delay, boolean hasUsedMana, int initialStacktraceSize, List<Fragment> arguments, Map<Pattern, SpellPart> importedTricks, List<Integer> stacktrace, Optional<MutableManaPool> poolOverride) {
        this.recursions = recursions;
        this.delay = delay;
        this.hasUsedMana = hasUsedMana;
        this.initialStacktraceSize = initialStacktraceSize;
        this.arguments = arguments;
        this.importedTricks = importedTricks;
        this.stacktrace.addAll(stacktrace);
        this.poolOverride = poolOverride;
    }

    public ExecutionState(List<Fragment> arguments) {
        this(0, 0, false, 0, arguments, new HashMap<>(), List.of(), Optional.empty());
    }

    public ExecutionState(List<Fragment> arguments, MutableManaPool poolOverride) {
        this(0, 0, false, 0, arguments, new HashMap<>(), List.of(), Optional.ofNullable(poolOverride));
    }

    private ExecutionState(int recursions, List<Fragment> arguments, Map<Pattern, SpellPart> importedTricks, Optional<MutableManaPool> poolOverride, Deque<Integer> stacktrace) {
        this(recursions, 0, false, stacktrace.size(), arguments, importedTricks, stacktrace.stream().toList(), poolOverride);
    }

    public ExecutionState recurseOrThrow(List<Fragment> arguments) throws ExecutionLimitReachedBlunder {
        if (recursions + 1 >= MAX_RECURSION_DEPTH) {
            throw new ExecutionLimitReachedBlunder();
        }

        var state = new ExecutionState(recursions + 1, arguments, importedTricks, poolOverride, stacktrace);
        state.stacktrace.push(-2);
        return state;
    }

    public void decrementRecursions() {
        recursions--;
        // Remove the function call instruction that was added by recursing from the stacktrace,
        // and add a tail recursion one instead.
        while (!stacktrace.isEmpty() && stacktrace.size() >= initialStacktraceSize) {
            stacktrace.pop();
        }

        if (stacktrace.isEmpty() || stacktrace.peek() != -3)
            stacktrace.push(-3);
    }

    public MutableManaPool tryOverridePool(MutableManaPool pool) {
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

    public int getInitialStacktraceSize() {
        return initialStacktraceSize;
    }

    public boolean hasUsedMana() {
        return hasUsedMana;
    }

    public void useMana(Trick trickSource, SpellContext ctx, float amount) throws NotEnoughManaBlunder {
        hasUsedMana = true;

        if (ctx.getManaPool().use(amount, ctx.source().getWorld()) > 0)
            throw new NotEnoughManaBlunder(trickSource, amount);
    }

    public Map<Pattern, SpellPart> getImportedTricks() {
        return importedTricks;
    }
}
