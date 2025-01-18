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
public class MapRemoveTrick extends Trick<MapRemoveTrick> {
    public MapRemoveTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8), Signature.of(FragmentType.MAP, variadic(Fragment.class), MapRemoveTrick::run));
    }

    public Fragment run(SpellContext ctx, MapFragment map, List<Fragment> keys) throws BlunderException {
        var newMap = map.map();

        for (var key : keys) {
            newMap = newMap.remove(key);
        }

        return new MapFragment(newMap);
    }
}
