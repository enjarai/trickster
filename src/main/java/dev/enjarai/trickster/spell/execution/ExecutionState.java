package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.blunder.ExecutionLimitReachedBlunder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ExecutionState {
    public static final int MAX_RECURSION_DEPTH = 255;
    public static final Codec<ExecutionState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("recursions").forGetter(ExecutionState::getRecursions),
            Fragment.CODEC.get().codec().listOf().fieldOf("arguments").forGetter(state -> state.arguments),
            Codec.INT.listOf().fieldOf("stacktrace").forGetter(state -> state.stacktrace.stream().toList())
    ).apply(instance, ExecutionState::new));

    protected int recursions;
    private final List<Fragment> arguments;
    private final Deque<Integer> stacktrace = new ArrayDeque<>();

    private ExecutionState(int recursions, List<Fragment> arguments, List<Integer> stacktrace) {
        this.recursions = recursions;
        this.arguments = arguments;
        this.stacktrace.addAll(stacktrace);
    }

    public ExecutionState(List<Fragment> arguments) {
        this(0, arguments, List.of());
    }

    private ExecutionState(int recursions, List<Fragment> arguments) {
        this(recursions, arguments, List.of());
    }

    public ExecutionState recurseOrThrow(List<Fragment> arguments) throws ExecutionLimitReachedBlunder {
        if (recursions + 1 >= MAX_RECURSION_DEPTH) {
            throw new ExecutionLimitReachedBlunder();
        }

        return new ExecutionState(recursions + 1, arguments);
    }

    public void decrementRecursion() {
        recursions--;
    }

    public int getRecursions() {
        return recursions;
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

    public Deque<Integer> getStacktrace() {
        return stacktrace;
    }
}
