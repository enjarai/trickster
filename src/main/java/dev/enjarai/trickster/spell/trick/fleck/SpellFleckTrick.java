package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class SpellFleckTrick extends AbstractFleckTrick<SpellFleckTrick> {
    public SpellFleckTrick() {
        super(
                Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 2, 5),
                Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, FragmentType.VECTOR, FragmentType.SPELL_PART, FragmentType.NUMBER.optionalOfArg(),
                        FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), SpellFleckTrick::run, FragmentType.NUMBER)
        );
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id, VectorFragment position, VectorFragment facing, SpellPart spell, Optional<NumberFragment> size,
            Optional<List<EntityFragment>> targets) {
        return display(ctx, id, new SpellFleck(
                position.vector().get(new Vector3f()),
                facing.vector().get(new Vector3f()),
                spell,
                size.map(NumberFragment::number).orElse(1.0).floatValue()
        ), targets);
    }
}
