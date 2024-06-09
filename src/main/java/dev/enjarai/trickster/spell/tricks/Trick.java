package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncompatibleTypesBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.MissingFragmentBlunder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public abstract class Trick {
    protected final Pattern pattern;

    protected Trick(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public abstract Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException;

    protected <T extends Fragment> T expectInput(List<Fragment> fragments, FragmentType<T> type, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, type.getName());
        }
        var fragment = fragments.get(index);
        if (fragment.type() != type) {
            throw new IncorrectFragmentBlunder(this, index, type.getName(), fragment.type());
        }
        //noinspection unchecked
        return (T) fragment;
    }

    protected <T extends Fragment> T expectInput(List<Fragment> fragments, Class<T> type, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of(type.getName()));
        }
        var fragment = fragments.get(index);
        return expectType(fragment, type);
    }

    protected <T extends Fragment> T expectType(Fragment fragment, Class<T> type) throws BlunderException {
        if (!type.isInstance(fragment)) {
            throw new IncompatibleTypesBlunder(this);
        }
        //noinspection unchecked
        return (T) fragment;
    }

    protected Fragment expectInput(List<Fragment> fragments, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of("any"));
        }
        return fragments.get(index);
    }

    public MutableText getName() {
        var id = Tricks.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath());
    }
}
