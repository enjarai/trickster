package dev.enjarai.trickster.spell.type;

import java.util.ArrayList;
import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class ClassVariadicArgType<T extends Fragment> implements VariadicArgType<T> {
    private final Class<T>[] types;
    private final boolean required;
    private final boolean unpack;

    public ClassVariadicArgType(Class<T>[] types, boolean required, boolean unpack) {
        this.types = types;
        this.required = required;
        this.unpack = unpack;
    }

    @SafeVarargs
    public ClassVariadicArgType(Class<T>... types) {
        this(types, false, false);
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return fragments.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        if (unpack && fragments.size() > 0 && fragments.get(0) instanceof ListFragment list) {
            fragments = list.fragments();
        }

        var result = new ArrayList<T>();

        for (var fragment : fragments) {
            result.add((T) fragment);
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (unpack && fragments.size() > 0 && fragments.get(0) instanceof ListFragment list) {
            fragments = list.fragments();
        }

        if (required && fragments.size() < 1) {
            return false;
        }

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
        return new ClassVariadicArgType<>(types) {
            @Override
            public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = ClassVariadicArgType.this.compose(trick, ctx, fragments);

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

    @Override
    public VariadicArgType<T> required() {
        return new ClassVariadicArgType<>(types, true, unpack);
    }

    @Override
    public VariadicArgType<T> unpack() {
        return new ClassVariadicArgType<>(types, required, true);
    }
}
