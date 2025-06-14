package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingFragmentBlunder;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.text.Text;

public class LoadArgumentTrick extends Trick<LoadArgumentTrick> {
    private final int index;

    public LoadArgumentTrick(Pattern pattern, int index) {
        super(pattern, Signature.of(LoadArgumentTrick::load, RetType.ANY));
        this.index = index;
    }

    public Fragment load(SpellContext ctx) throws BlunderException {
        if (ctx.state().getArguments().size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of("any"));
        }

        return ctx.state().getArguments().get(index);
    }
}
