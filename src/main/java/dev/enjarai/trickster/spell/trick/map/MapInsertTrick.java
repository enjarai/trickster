package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.vavr.collection.HashMap;

import java.util.List;

//TODO: distortion?
public class MapInsertTrick extends Trick<MapInsertTrick> {
    public MapInsertTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 8), Signature.of(map(Fragment.class, Fragment.class), variadic(Fragment.class, Fragment.class).require(), MapInsertTrick::run, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public MapFragment run(SpellContext ctx, HashMap<Fragment, Fragment> map, List<Fragment> pairs) throws BlunderException {
        for (int i = 0; i < pairs.size(); i += 2) {
            map = map.put(pairs.get(i), pairs.get(i + 1));
        }

        return new MapFragment(map);
    }
}
