package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class SimpleArgType<T extends Fragment> implements ArgType<T> {
    private final Class<T> type;

    public SimpleArgType(Class<T> type) {
        this.type = type;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        return (T) fragments.get(0);
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        return type.isInstance(fragments.get(0));
    }

    @Override
    public ArgType<T> wardOf() {
        return new SimpleArgType<>(type) {
            @Override
            public T compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = SimpleArgType.this.compose(trick, ctx, fragments);

                if (result instanceof EntityFragment entity) {
                    ArgType.tryWard(trick, ctx, entity, fragments);
                }

                return result;
            }

            @Override
            public ArgType<T> wardOf() {
                return this;
            }
        };
    }

    @Override
    public MutableText asText() {
        return Text.translatableWithFallback("trickster.fragment.class." + type.getSimpleName(), type.getSimpleName()).withColor(0xaa4444);
    }
}
