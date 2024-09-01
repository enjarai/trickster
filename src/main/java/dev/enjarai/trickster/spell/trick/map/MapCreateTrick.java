package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;
import java.util.Map;

public class MapCreateTrick extends Trick {
    public MapCreateTrick() {
        super(Pattern.of(2, 5, 8, 6, 3, 0 ));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new MapFragment(Hamt.fromMap(Map.of(
                BooleanFragment.TRUE, new NumberFragment(12.9),
                BooleanFragment.FALSE, new NumberFragment(16.4),
                new NumberFragment(22), new ListFragment(List.of(BooleanFragment.TRUE, BooleanFragment.TRUE, BooleanFragment.TRUE, BooleanFragment.FALSE))
        )));
    }
}
