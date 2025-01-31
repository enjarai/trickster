package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ClassListArgType<T extends Fragment> extends AbstractListArgType<T, Class<T>> {
    @SafeVarargs
    public ClassListArgType(Class<T>... types) {
        super(types);
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new ClassListArgType<>(types) {
            @Override
            public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = ClassListArgType.this.compose(trick, ctx, fragments);

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
    protected boolean matchType(Class<T> type, Fragment fragment) {
        return type.isInstance(fragment);
    }

    @Override
    protected MutableText typeAsText(Class<T> type) {
        return Text.translatableWithFallback("trickster.fragment.class." + type.getSimpleName(), type.getSimpleName()).withColor(0xaa4444);
    }
}
