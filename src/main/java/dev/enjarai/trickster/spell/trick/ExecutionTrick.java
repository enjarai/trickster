package dev.enjarai.trickster.spell.trick;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidArgumentsBlunder;

import java.util.List;

public abstract class ExecutionTrick<T extends ExecutionTrick<T>> extends Trick<T> {
    private final List<Signature<T, SpellExecutor>> handlers;

    public ExecutionTrick(Pattern pattern, List<Signature<T, SpellExecutor>> handlers) {
        super(pattern);
        this.handlers = handlers;
    }

    public ExecutionTrick(Pattern pattern, Signature<T, SpellExecutor> primary) {
        this(pattern);
        this.handlers.add(primary);
    }

    public ExecutionTrick(Pattern pattern) {
        this(pattern, List.of());
    }

    @Override
    public Trick<T> overload(Signature<T, Fragment> signature) {
        throw new UnsupportedOperationException("Use overloadExecutor to overload an AbstractExecutionTrick");
    }

    public ExecutionTrick<T> overloadExecutor(Signature<T, SpellExecutor> signature) {
        this.handlers.add(signature);
        return this;
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) {
        return makeExecutor(ctx, fragments).singleTickRun(ctx);
    }

    @SuppressWarnings("unchecked")
    public SpellExecutor makeExecutor(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        for (int i = handlers.size(); i >= 0; i--) {
            var handler = handlers.get(i);

            if (handler.match(fragments)) {
                return handler.run((T) this, ctx, fragments);
            }
        }

        throw new InvalidArgumentsBlunder(this);
    }
}
