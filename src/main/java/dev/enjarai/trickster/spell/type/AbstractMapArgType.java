package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.collection.HashMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class AbstractMapArgType<K extends Fragment, V extends Fragment, KT extends T, VT extends T, T> implements ArgType<HashMap<K, V>> {
    protected final KT keyType;
    protected final VT valueType;

    public AbstractMapArgType(KT keyType, VT valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return FragmentType.MAP.argc(fragments);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HashMap<K, V> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        var map = FragmentType.MAP.compose(trick, ctx, fragments);
        var result = HashMap.<K, V>empty();

        for (var entry : map.map()) {
            result = result.put((K) entry._1, (V) entry._2);
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (!FragmentType.MAP.match(fragments)) {
            return false;
        }

        var map = (MapFragment) fragments.get(0);

        for (var entry : map.map()) {
            if (!matchType(keyType, entry._1)) {
                return false;
            }

            if (!matchType(valueType, entry._2)) {
                return false;
            }
        }

        return true;
    }

    protected abstract boolean matchType(T type, Fragment fragment);

    @Override
    public MutableText asText() {
        return Text.literal("{ ")
                .append(typeAsText(keyType))
                .append(": ")
                .append(typeAsText(valueType))
                .append(" }");
    }

    protected abstract MutableText typeAsText(T type);
}
