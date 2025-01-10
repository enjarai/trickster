package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import java.util.List;

//TODO: distortion?
public class MapInsertTrick extends Trick<MapInsertTrick> {
    public MapInsertTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 8), Signature.of(FragmentType.MAP, variadic(Fragment.class, Fragment.class), MapInsertTrick::run));
    }

    public Fragment run(SpellContext ctx, MapFragment map, List<Fragment> pairs) throws BlunderException {
        var newMap = map.map();

        for (int i = 0; i < pairs.size(); i += 2) {
            newMap = newMap.put(pairs.get(i), pairs.get(i + 1));
        }

        return new MapFragment(newMap);
    }
}
