package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class VariadicArgType<T> implements ArgType<List<T>> {
    protected final ArgType<T> type;
    protected final boolean require;
    protected final boolean unpack;

    public VariadicArgType(ArgType<T> type, boolean require, boolean unpack) {
        this.type = type;
        this.require = require;
        this.unpack = unpack;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return fragments.size() - (fragments.size() % type.argc(fragments));
    }

    @Override
    public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        if (unpack && !fragments.isEmpty() && fragments.getFirst() instanceof ListFragment list) {
            fragments = list.fragments();
        }

        var argc = type.argc(fragments);
        var result = new ArrayList<T>();

        if (argc != 0) {
            for (int i = 0; i < fragments.size() / argc; i++) {
                result.add(type.compose(trick, ctx, isolate(i * argc, fragments)));
            }
        }

        return result;
    }

    public VariadicArgType<T> require() {
        return new VariadicArgType<>(type, true, unpack);
    }

    public VariadicArgType<T> unpack() {
        return new VariadicArgType<>(type, require, true);
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (unpack && !fragments.isEmpty() && fragments.getFirst() instanceof ListFragment list) {
            fragments = list.fragments();
        }

        if (require && fragments.isEmpty()) {
            return false;
        }

        var argc = type.argc(fragments);
        if (argc == 0) {
            return false;
        }

        if (fragments.size() % argc != 0) {
            return false;
        }

        for (int i = 0; i < fragments.size() / argc; i++) {
            if (!type.match(isolate(i * argc, fragments))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new VariadicArgType<>(type.wardOf(), require, unpack);
    }

    @Override
    public MutableText asText() {
        var typeText = type.asText();
        var text = Text.empty().append(typeText).append("...");

        if (unpack) {
            text = text.append(" | [ ").append(typeText).append(" ]");
        }

        return text;
    }
}
