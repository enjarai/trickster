package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.collection.HashMap;
import net.minecraft.text.MutableText;

public class TypeMapArgType<K extends Fragment, V extends Fragment> extends AbstractMapArgType<K, V, FragmentType<K>, FragmentType<V>, FragmentType<?>> {
    public TypeMapArgType(FragmentType<K> keyType, FragmentType<V> valueType) {
        super(keyType, valueType);
    }

    @Override
    public ArgType<HashMap<K, V>> wardOf() {
        return new TypeMapArgType<>(keyType, valueType) {
            @Override
            public HashMap<K, V> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = TypeMapArgType.this.compose(trick, ctx, fragments);

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
    protected boolean matchType(FragmentType<?> type, Fragment fragment) {
        return fragment.type() == type;
    }

    @Override
    protected MutableText typeAsText(FragmentType<?> type) {
        return type.asText();
    }
}
