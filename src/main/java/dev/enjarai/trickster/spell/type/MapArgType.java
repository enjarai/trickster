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

public class MapArgType<K, V> implements ArgType<HashMap<K, V>> {
    protected final ArgType<K> keyType;
    protected final ArgType<V> valueType;

    public MapArgType(ArgType<K> keyType, ArgType<V> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return FragmentType.MAP.argc(fragments);
    }

    @Override
    public HashMap<K, V> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        var map = FragmentType.MAP.compose(trick, ctx, fragments);
        var result = HashMap.<K, V>empty();

        for (var entry : map.map()) {
            result = result.put(
                    keyType.compose(trick, ctx, List.of(entry._1)),
                    valueType.compose(trick, ctx, List.of(entry._2))
            );
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (!FragmentType.MAP.match(fragments)) {
            return false;
        }

        var map = (MapFragment) fragments.getFirst();

        var argcKey = keyType.argc(fragments);
        var argcValue = valueType.argc(fragments);

        if (argcKey != 1 || argcValue != 1) {
            return false;
        }

        for (var entry : map.map()) {
            if (!keyType.match(List.of(entry._1))) {
                return false;
            }

            if (!valueType.match(List.of(entry._2))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ArgType<HashMap<K, V>> wardOf() {
        return new MapArgType<>(keyType.wardOf(), valueType.wardOf());
    }

    @Override
    public MutableText asText() {
        return Text.literal("{")
                .append(keyType.asText())
                .append(": ")
                .append(valueType.asText())
                .append("}");
    }
}
