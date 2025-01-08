package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class ModuloTrick extends DistortionTrick<ModuloTrick> {
    public ModuloTrick() {
        super(Pattern.of(0, 4, 1, 2, 4, 6, 7, 4, 8), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, ModuloTrick::math));
    }

    public Fragment math(SpellContext ctx, NumberFragment param1, NumberFragment param2) throws BlunderException {
        return new NumberFragment(param1.number() % param2.number());
    }
}
