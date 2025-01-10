package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TypeVariadicArgType<T extends Fragment> implements VariadicArgType<T> {
    private final FragmentType<T>[] types;
    private final boolean required;
    private final boolean unpack;

    public TypeVariadicArgType(FragmentType<T>[] types, boolean required, boolean unpack) {
        this.types = types;
        this.required = required;
        this.unpack = unpack;
    }

    @SafeVarargs
    public TypeVariadicArgType(FragmentType<T>... types) {
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
            if (types[offset % types.length] != fragment.type()) {
                return false;
            }

            offset++;
        }

        return true;
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new TypeVariadicArgType<>(types) {
            @Override
            public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = TypeVariadicArgType.this.compose(trick, ctx, fragments);

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
        return new TypeVariadicArgType<>(types, true, unpack);
    }

    @Override
    public VariadicArgType<T> unpack() {
        return new TypeVariadicArgType<>(types, required, true);
    }

    @Override
    public MutableText asText() {
        if (types.length == 1) {
            return types[0].asText().append("...");
        }

        var text = Text.literal("[");
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            if (i > 0) {
                text = text.append(", ");
            }
            text = text.append(type.asText());
        }
        return text.append("]...");
    }
}
