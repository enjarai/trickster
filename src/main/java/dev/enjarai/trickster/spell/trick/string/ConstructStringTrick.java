package dev.enjarai.trickster.spell.trick.string;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class ConstructStringTrick extends Trick {
    public ConstructStringTrick() {
        super(Pattern.of(1, 4, 3, 0, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new StringFragment(fragments.stream()
                .map(fragment -> fragment.isEphemeral() ? new ZalgoFragment() : fragment)
                .map(fragment -> fragment.asText().copy())
                .reduce(Text.literal(""), MutableText::append));
    }
}
