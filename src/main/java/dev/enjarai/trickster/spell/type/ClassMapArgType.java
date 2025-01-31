package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.collection.HashMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ClassMapArgType<K extends Fragment, V extends Fragment> extends AbstractMapArgType<K, V, Class<K>, Class<V>, Class<?>> {
    public ClassMapArgType(Class<K> keyType, Class<V> valueType) {
        super(keyType, valueType);
    }

    @Override
    public ArgType<HashMap<K, V>> wardOf() {
        return new ClassMapArgType<>(keyType, valueType) {
            @Override
            public HashMap<K, V> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = ClassMapArgType.this.compose(trick, ctx, fragments);

                for (var entry : result) {
                    if (entry._1 instanceof EntityFragment entity) {
                        ArgType.tryWard(trick, ctx, entity, fragments);
                    }

                    if (entry._2 instanceof EntityFragment entity) {
                        ArgType.tryWard(trick, ctx, entity, fragments);
                    }
                }

                return result;
            }

            @Override
            public ArgType<HashMap<K, V>> wardOf() {
                return this;
            }
        };
    }

    @Override
    protected boolean matchType(Class<?> type, Fragment fragment) {
        return type.isInstance(fragment);
    }

    @Override
    protected MutableText typeAsText(Class<?> type) {
        return Text.translatableWithFallback("trickster.fragment.class." + type.getSimpleName(), type.getSimpleName()).withColor(0xaa4444);
    }
}
