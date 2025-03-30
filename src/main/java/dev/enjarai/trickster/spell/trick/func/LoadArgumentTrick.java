package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingFragmentBlunder;
import net.minecraft.text.Text;

import java.util.List;

public class LoadArgumentTrick extends Trick<LoadArgumentTrick> {
    private final int index;

    public LoadArgumentTrick(Pattern pattern, int index) {
        super(pattern);
        this.index = index;
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (ctx.state().getArguments().size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of("any"));
        }

        return ctx.state().getArguments().get(index);
    }
}
