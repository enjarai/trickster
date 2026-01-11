package dev.enjarai.trickster.spell.trick.ward;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.fragment.WardFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class DeleteWardTrick extends Trick<DeleteWardTrick> {
    public DeleteWardTrick() {
        //TODO: pattern
        super(Pattern.of(), Signature.of(FragmentType.WARD, DeleteWardTrick::delete, FragmentType.VOID));
    }

    public VoidFragment delete(SpellContext ctx, WardFragment ward) {
        //TODO: impl
        return VoidFragment.INSTANCE;
    }
}
