package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListArgType<F extends Fragment, T> implements ArgType<List<F>> {
    protected final T[] types;

    protected AbstractListArgType(T[] types) {
        this.types = types;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return FragmentType.LIST.argc(fragments);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<F> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        var list = FragmentType.LIST.compose(trick, ctx, fragments);
        var result = new ArrayList<F>();

        for (var fragment : list.fragments()) {
            result.add((F) fragment);
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (!FragmentType.LIST.match(fragments)) {
            return false;
        }

        var list = ((ListFragment) fragments.get(0)).fragments();

        if (list.size() % types.length != 0) {
            return false;
        }

        int offset = 0;

        for (var fragment : list) {
            if (!matchType(types[offset % types.length], fragment)) {
                return false;
            }

            offset++;
        }

        return true;
    }

    protected abstract boolean matchType(T type, Fragment fragment);

    @Override
    public MutableText asText() {
        var text = Text.literal("[");

        for (int i = 0; i < types.length; i++) {
            T type = types[i];

            if (i > 0) {
                text = text.append(", ");
            }

            text = text.append(typeAsText(type));
        }

        return text.append("]");
    }

    protected abstract MutableText typeAsText(T type);
}
