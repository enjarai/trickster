package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetAllArgumentsTrick extends Trick<GetAllArgumentsTrick> {
    public GetAllArgumentsTrick() {
        super(Pattern.of(3, 4, 5, 2, 0, 3, 6, 8, 5), Signature.of(GetAllArgumentsTrick::run));
    }

    public Fragment run(SpellContext ctx) {
        return new ListFragment(ctx.state().getArguments());
    }
}
