package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

import java.util.List;

public class InvertTrick extends DistortionTrick<InvertTrick> {
    public InvertTrick() {
        super(Pattern.of(3, 4, 5, 2, 3), List.of(
                Signature.of(FragmentType.NUMBER, InvertTrick::invert, FragmentType.NUMBER),
                Signature.of(FragmentType.VECTOR, InvertTrick::invert, FragmentType.VECTOR)
        ));
    }

    public NumberFragment invert(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(-number.number());
    }

    public VectorFragment invert(SpellContext ctx, VectorFragment vec) throws BlunderException {
        return new VectorFragment(vec.vector().mul(-1, new Vector3d()));
    }
}
