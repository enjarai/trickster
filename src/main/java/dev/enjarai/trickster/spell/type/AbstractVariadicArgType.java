package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVariadicArgType<T extends Fragment, A> implements VariadicArgType<T> {
    protected final A[] types;
    protected final boolean required;
    protected final boolean unpack;

    protected AbstractVariadicArgType(A[] types, boolean required, boolean unpack) {
        this.types = types;
        this.required = required;
        this.unpack = unpack;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return fragments.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        if (unpack && !fragments.isEmpty() && fragments.getFirst() instanceof ListFragment list) {
            fragments = list.fragments();
        }

        var result = new ArrayList<T>();

        for (var fragment : fragments) {
            result.add((T) fragment);
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (unpack && !fragments.isEmpty() && fragments.getFirst() instanceof ListFragment list) {
            fragments = list.fragments();
        }

        if (required && fragments.isEmpty()) {
            return false;
        }

        if (fragments.size() % types.length != 0) {
            return false;
        }

        int offset = 0;

        for (var fragment : fragments) {
            if (!matchType(types[offset % types.length], fragment)) {
                return false;
            }

            offset++;
        }

        return true;
    }

    protected abstract boolean matchType(A type, Fragment fragment);

    protected MutableText appendTypesText(MutableText text) {
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            if (i > 0) {
                text = text.append(", ");
            }
            text = text.append(typeAsText(type));
        }
        return text;
    }

    @Override
    public MutableText asText() {
        if (types.length == 1) {
            return typeAsText(types[0]).append("...");
        }

        var text = appendTypesText(Text.literal("(")).append(")...");

        if (unpack) {
            text = appendTypesText(text.append(" | [")).append("]");
        }

        return text;
    }

    protected abstract MutableText typeAsText(A type);
}
