package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.MissingInputsBlunder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapInsertTrick extends Trick {
    public MapInsertTrick() {
        super(Pattern.of(/*todo*/));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Iterator<Fragment> fragmentIterator = fragments.iterator();

        var first = fragmentIterator.next();
        if (!(first instanceof MapFragment)) {
            throw new IncorrectFragmentBlunder(this, 0, FragmentType.MAP.getName(), first);
        }
        var map = ((MapFragment) first).map();

        while(fragmentIterator.hasNext()) {
            Fragment key = fragmentIterator.next();
            if (fragmentIterator.hasNext()) {
                Fragment value = fragmentIterator.next();
                map.assoc(key, value);
            } else {
                throw new MissingInputsBlunder(this);
            }
        }

        return new MapFragment(map);
    }
}
