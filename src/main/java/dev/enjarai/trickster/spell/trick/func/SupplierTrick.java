package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

public class SupplierTrick extends Trick<SupplierTrick> {
    public SupplierTrick() {
        super(Pattern.of(0, 1, 2, 5, 8, 7, 6, 3, 0), Signature.of(ArgType.ANY, SupplierTrick::run, FragmentType.SPELL_PART));
    }

    public SpellPart run(SpellContext ctx, Fragment glyph) {
        return new SpellPart(glyph);
    }
}
