package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingFragmentBlunder;
import net.minecraft.text.Text;

import java.util.List;

public class LoadArgumentTrick extends Trick {
    private final int index;

    public LoadArgumentTrick(Pattern pattern, int index) {
        super(pattern);
        this.index = index;
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (ctx.peekPartGlyph().size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of("any"));
        }

        return ctx.peekPartGlyph().get(index);
    }
}
