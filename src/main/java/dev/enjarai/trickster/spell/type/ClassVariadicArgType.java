package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ClassVariadicArgType<T extends Fragment> extends AbstractVariadicArgType<T, Class<T>> {
    public ClassVariadicArgType(Class<T>[] types, boolean required, boolean unpack) {
        super(types, required, unpack);
    }

    @SafeVarargs
    public ClassVariadicArgType(Class<T>... types) {
        this(types, false, false);
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
    public AbstractVariadicArgType<T, Class<T>> require() {
        return new ClassVariadicArgType<>(types, true, unpack);
    }

    @Override
    public AbstractVariadicArgType<T, Class<T>> unpack() {
        return new ClassVariadicArgType<>(types, require, true);
    }

    @Override
    protected boolean matchType(Class<T> type, Fragment fragment) {
        return type.isInstance(fragment);
    }

    @Override
    protected MutableText typeAsText(Class<T> type) {
        return Text.translatableWithFallback("trickster.fragment.class." + type.getSimpleName(), type.getSimpleName()).withColor(0xaa4444);
    }
}
