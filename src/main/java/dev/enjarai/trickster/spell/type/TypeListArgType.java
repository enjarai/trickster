package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class TypeListArgType<T extends Fragment> extends AbstractListArgType<T, FragmentType<T>> {
    @SafeVarargs
    public TypeListArgType(FragmentType<T>... types) {
        super(types);
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new TypeListArgType<>(types) {
            @Override
            public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = TypeListArgType.this.compose(trick, ctx, fragments);

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
    protected boolean matchType(FragmentType<T> type, Fragment fragment) {
        return fragment.type() == type;
    }

    @Override
    protected MutableText typeAsText(FragmentType<T> type) {
        return type.asText();
    }

}
