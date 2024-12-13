package dev.enjarai.trickster.spell.type;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.spell.Fragment;

public interface ArgType<T> {
    int argc(List<Fragment> fragments);
    @Nullable T compose(List<Fragment> fragments);

    default boolean match(List<Fragment> fragments) {
        return compose(fragments) != null;
    }

    default List<Fragment> isolate(int start, List<Fragment> fragments) {
        return fragments.subList(start, argc(fragments));
    }

    default ArgType<Optional<T>> optionalOf() {
        return new ArgType<>() {
            @Override
            public int argc(List<Fragment> fragments) {
                return Math.min(fragments.size(), 1);
            }

            @Override
            @Nullable
            public Optional<T> compose(List<Fragment> fragments) {
                if (argc(fragments) == 0) {
                    return Optional.empty();
                }

                var result = ArgType.this.compose(fragments);

                if (result == null) {
                    return null;
                }

                return Optional.of(result);
            }
        };
    }
}
