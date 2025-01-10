package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.spell.fleck.LineFleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class LineFleckTrick extends AbstractFleckTrick<LineFleckTrick> {
    public LineFleckTrick() {
        super(Pattern.of(2, 5, 7, 4, 3, 1, 2), Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, FragmentType.VECTOR, variadic(FragmentType.ENTITY).unpack().optionalOf(), LineFleckTrick::run));
    }

    public Fragment run(SpellContext ctx, NumberFragment id, VectorFragment pos1, VectorFragment pos2, Optional<List<EntityFragment>> targets) throws BlunderException {
        return display(ctx, id, new LineFleck(pos1.vector().get(new Vector3f()), pos2.vector().get(new Vector3f())), targets);

    }
}
