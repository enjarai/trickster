package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class OtherHandTrick extends Trick<OtherHandTrick> {
    public OtherHandTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4), Signature.of(OtherHandTrick::run, FragmentType.ITEM_TYPE));
    }

    public ItemTypeFragment run(SpellContext ctx) {
        return new ItemTypeFragment(
                ctx.source().getOtherHandStack()
                        .orElseThrow(() -> new MissingItemBlunder(this)).getItem()
        );
    }
}
