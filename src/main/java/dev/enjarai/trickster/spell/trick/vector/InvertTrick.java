package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class InvertTrick extends Trick {
    public InvertTrick() {
        super(Pattern.of(3, 4, 5, 2, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vec = supposeInput(fragments, FragmentType.VECTOR, 0);
        if (vec.isPresent()) {
            return new VectorFragment(vec.get().vector().mul(-1, new Vector3d()));
        }

        var number = expectInput(fragments, FragmentType.NUMBER, 0);
        return new NumberFragment(-number.number());
    }
}