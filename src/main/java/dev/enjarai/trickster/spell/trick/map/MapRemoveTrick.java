package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class MapRemoveTrick extends Trick {
    public MapRemoveTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var map = expectInput(fragments, MapFragment.class, 0).downcast();
        Fragment key = expectInput(fragments, 1);
        return new MapFragment(map.dissoc(key));
    }
}
