package dev.enjarai.trickster.spell.type;

import java.util.ArrayList;
import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class VariadicArgType<T extends Fragment> implements ArgType<List<T>> {
    private final Class<T>[] types;

    @SafeVarargs
    public VariadicArgType(Class<T>... types) {
        this.types = types;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return fragments.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        var result = new ArrayList<T>();

        for (var fragment : fragments) {
            result.add((T) fragment);
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (fragments.size() % types.length != 0) {
            return false;
        }

        int offset = 0;

        for (var fragment : fragments) {
            if (!types[offset % types.length].isInstance(fragment)) {
                return false;
            }

            offset++;
        }

        return true;
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new VariadicArgType<>(types) {
            @Override
            public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = VariadicArgType.this.compose(trick, ctx, fragments);

                for (var fragment : result) {
                    if (fragment instanceof EntityFragment entity) {
                        ArgType.tryWard(trick, ctx, entity, fragments);
                    }
                }

                return result;
            }

            @Override
            public ArgType<List<T>> wardOf() {
                return this;
            }
        };
    }
}
