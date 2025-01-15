package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import java.util.List;

public class TypeVariadicArgType<T extends Fragment> extends AbstractVariadicArgType<T, FragmentType<T>> {
    public TypeVariadicArgType(FragmentType<T>[] types, boolean required, boolean unpack) {
        super(types, required, unpack);
    }

    @SafeVarargs
    public TypeVariadicArgType(FragmentType<T>... types) {
        this(types, false, false);
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
    public AbstractVariadicArgType<T, FragmentType<T>> require() {
        return new TypeVariadicArgType<>(types, true, unpack);
    }

    @Override
    public AbstractVariadicArgType<T, FragmentType<T>> unpack() {
        return new TypeVariadicArgType<>(types, required, true);
    }

    @Override
    protected boolean matchType(FragmentType<T> type, Fragment fragment) {
        return type == fragment.type();
    }

    @Override
    protected MutableText typeAsText(FragmentType<T> type) {
        return type.asText();
    }
}
