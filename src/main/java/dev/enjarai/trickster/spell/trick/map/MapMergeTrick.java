package dev.enjarai.trickster.spell.trick.map;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

public class MapMergeTrick extends DistortionTrick {
    public MapMergeTrick() {
        super(Pattern.of(2, 4, 8, 2, 5, 8, 6, 3, 0));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        MapFragment result = null;
        for (int i = 0; i < fragments.size(); i++) {
            var value = expectType(fragments.get(i), MapFragment.class, i);
            if (result == null) {
                result = value;
            } else {
                result = result.mergeWith(value);
            }
        }

        if (result == null) {
            throw new MissingInputsBlunder(this);
        }

        return result;
    }
}
