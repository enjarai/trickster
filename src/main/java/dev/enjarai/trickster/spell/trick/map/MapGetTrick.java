package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.MissingInputsBlunder;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MapGetTrick extends Trick {
    public MapGetTrick() {
        super(Pattern.of(/*todo*/));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var map = expectInput(fragments, MapFragment.class, 0).map();
        Fragment key = expectInput(fragments, 1);

        return map.get(key).orElse(VoidFragment.INSTANCE);
    }
}
