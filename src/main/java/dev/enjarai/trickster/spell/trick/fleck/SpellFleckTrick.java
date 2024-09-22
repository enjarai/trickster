package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import org.joml.Vector3f;

import java.util.List;

public class SpellFleckTrick extends AbstractFleckTrick {
    public SpellFleckTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 2, 5));
    }

    @Override
    public SpellFleck makeFleck(SpellContext ctx, List<Fragment> fragments) {
        var position = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var facing = expectInput(fragments, FragmentType.VECTOR, 1).vector();
        var spell = expectInput(fragments, FragmentType.SPELL_PART, 2);

        return new SpellFleck(position.get(new Vector3f()), facing.get(new Vector3f()), spell);
    }
}
