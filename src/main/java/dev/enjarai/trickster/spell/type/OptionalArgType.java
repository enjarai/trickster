package dev.enjarai.trickster.spell.type;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;

public class OptionalArgType<T> implements ArgType<Optional<T>> {
    private final ArgType<T> type;

    public OptionalArgType(ArgType<T> type) {
        this.type = type;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return Math.min(fragments.size(), 1);
    }

    @Override
    public Optional<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        if (argc(fragments) == 0) {
            return Optional.empty();
        }

        return Optional.of(type.compose(trick, ctx, fragments));
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (argc(fragments) == 0) {
            return true;
        }

        return type.match(fragments);
    }

    @Override
    public ArgType<Optional<T>> wardOf() {
        return new OptionalArgType<>(type.wardOf());
    }
}
