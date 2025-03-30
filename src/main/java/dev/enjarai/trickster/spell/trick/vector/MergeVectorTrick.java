package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import org.joml.Vector3d;

public class MergeVectorTrick extends DistortionTrick<MergeVectorTrick> {
    public MergeVectorTrick() {
        super(Pattern.of(1, 3, 4, 5, 1, 4, 7), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, FragmentType.NUMBER, MergeVectorTrick::merge));
    }

    public Fragment merge(SpellContext ctx, NumberFragment x, NumberFragment y, NumberFragment z) throws BlunderException {
        return new VectorFragment(new Vector3d(x.number(), y.number(), z.number()));
    }
}
