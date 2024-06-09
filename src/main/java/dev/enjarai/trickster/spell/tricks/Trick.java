package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncorrectFragmentException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingFragmentException;
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
            throw new MissingFragmentException(this, index, type);
        }
        var fragment = fragments.get(index);
        if (fragment.type() != type) {
            throw new IncorrectFragmentException(this, index, type, fragment.type());
        }
        //noinspection unchecked
        return (T) fragment;
    }

    public MutableText getName() {
        var id = Tricks.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath());
    }
}
