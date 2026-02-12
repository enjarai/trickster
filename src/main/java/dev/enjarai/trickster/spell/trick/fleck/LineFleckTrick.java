package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.fleck.LineFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class LineFleckTrick extends AbstractFleckTrick<LineFleckTrick> {
    public LineFleckTrick() {
        super(Pattern.of(2, 5, 7, 4, 3, 1, 2),
                Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, FragmentType.VECTOR, FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), LineFleckTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id, VectorFragment pos1, VectorFragment pos2, Optional<List<EntityFragment>> targets) {
        var rand = LineFleck.colorsRandom;
        rand.setSeed(id.asInt());
        var color = ColorHelper.Argb.fromFloats(1f, rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        return display(ctx, id, new LineFleck(pos1.vector().get(new Vector3f()), pos2.vector().get(new Vector3f()), 1f, color), targets);
    }
}
