package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

import java.util.List;

public class MapMergeTrick extends DistortionTrick<MapMergeTrick> {
    public MapMergeTrick() {
        super(Pattern.of(2, 4, 8, 2, 5, 8, 6, 3, 0), Signature.of(ArgType.ANY.mappedTo(ArgType.ANY).variadicOfArg().require().unpack(), MapMergeTrick::run, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public HashMap<Fragment, Fragment> run(SpellContext ctx, List<HashMap<Fragment, Fragment>> maps) throws BlunderException {
        var baseMap = HashMap.<Fragment, Fragment>empty();

        for (var map : maps) {
            baseMap = baseMap.merge(map);
        }

        return baseMap;
    }
}
