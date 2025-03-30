package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;

public class OtherHandTrick extends Trick<OtherHandTrick> {
    public OtherHandTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4), Signature.of(OtherHandTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        return new ItemTypeFragment(
                ctx.source().getOtherHandStack()
                        .orElseThrow(() -> new MissingItemBlunder(this)).getItem()
        );
    }
}
