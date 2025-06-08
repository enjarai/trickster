package dev.enjarai.trickster.spell.trick.map;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

public class MapMergeTrick extends DistortionTrick<MapMergeTrick> {
    public MapMergeTrick() {
        super(Pattern.of(2, 4, 8, 2, 5, 8, 6, 3, 0), Signature.of(map(Fragment.class, Fragment.class), variadic(FragmentType.MAP), MapMergeTrick::run, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public MapFragment run(SpellContext ctx, HashMap<Fragment, Fragment> baseMap, List<MapFragment> maps) throws BlunderException {
        for (var map : maps) {
            baseMap = baseMap.merge(map.map());
        }

        return new MapFragment(baseMap);
    }
}
