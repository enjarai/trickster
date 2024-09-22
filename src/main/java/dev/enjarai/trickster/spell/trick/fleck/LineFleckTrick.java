package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.fleck.LineFleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import org.joml.Vector3f;

import java.util.List;

public class LineFleckTrick extends AbstactFleckTrick {
    public LineFleckTrick() {
        super(Pattern.of(2, 5, 7, 4, 3, 1, 2));
    }

    @Override
    public LineFleck makeFleck(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos1 = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var pos2 = expectInput(fragments, FragmentType.VECTOR, 1).vector();

       return new LineFleck( pos1.get(new Vector3f()), pos2.get(new Vector3f()));
    }
}
