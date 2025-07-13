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

public class ListArgType<T> implements ArgType<List<T>> {
    protected final ArgType<T> type;

    protected ListArgType(ArgType<T> type) {
        this.type = type;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return FragmentType.LIST.argc(fragments);
    }

    @Override
    public List<T> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        var list = FragmentType.LIST.compose(trick, ctx, fragments).fragments();

        var argc = type.argc(list);
        var result = new ArrayList<T>();

        if (argc != 0) {
            for (int i = 0; i < list.size() / argc; i++) {
                result.add(type.compose(trick, ctx, isolate(i * argc, list)));
            }
        }

        return result;
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        if (!FragmentType.LIST.match(fragments)) {
            return false;
        }

        var list = ((ListFragment) fragments.getFirst()).fragments();

        var argc = type.argc(list);
        if (argc == 0) {
            return false;
        }

        if (list.size() % argc != 0) {
            return false;
        }

        for (int i = 0; i < list.size() / argc; i++) {
            if (!type.match(isolate(i * argc, list))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ArgType<List<T>> wardOf() {
        return new ListArgType<>(type.wardOf());
    }

    @Override
    public MutableText asText() {
        return Text.literal("[").append(type.asText()).append("]");
    }
}
