package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.tricks.blunder.ExecutionLimitReachedBlunder;

public class ExecutionContext {
    public static final int MAX_RECURSION_DEPTH = 255;
    public static final Codec<ExecutionContext> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        Codec.INT.fieldOf("recursions").forGetter(ExecutionContext::getRecursions)
      ).apply(instance, ExecutionContext::new)
    );

    protected int recursions;

    private ExecutionContext(int recursions) {
        this.recursions = recursions;
    }

    public ExecutionContext() {
        this(0);
    }

    public void recurseOrThrow() throws ExecutionLimitReachedBlunder {
        recursions++;

        if (recursions >= MAX_RECURSION_DEPTH)
            throw new ExecutionLimitReachedBlunder();
    }

    public void decrementRecursion() {
        recursions--;
    }

    public int getRecursions() {
        return recursions;
    }
}
