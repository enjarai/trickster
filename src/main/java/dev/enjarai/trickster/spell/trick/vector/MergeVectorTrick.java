package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class MergeVectorTrick extends Trick {
    public MergeVectorTrick() {
        super(Pattern.of(1, 3, 4, 5, 1, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vectorFragments = fragments;

        var list = supposeInput(fragments, 0).flatMap(l -> supposeType(l, FragmentType.LIST));
        if (list.isPresent()) {
            vectorFragments = list.get().fragments();
        }

        var x = expectInput(vectorFragments, FragmentType.NUMBER, 0);
        var y = expectInput(vectorFragments, FragmentType.NUMBER, 1);
        var z = expectInput(vectorFragments, FragmentType.NUMBER, 2);

        return new VectorFragment(new Vector3d(x.number(), y.number(), z.number()));
    }
}
