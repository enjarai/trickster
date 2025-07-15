package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

import java.util.List;

//TODO: distortion?
public class MapRemoveTrick extends Trick<MapRemoveTrick> {
    public MapRemoveTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8), Signature.of(ArgType.ANY.mappedTo(ArgType.ANY), ArgType.ANY.variadicOfArg().unpack(), MapRemoveTrick::run, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public HashMap<Fragment, Fragment> run(SpellContext ctx, HashMap<Fragment, Fragment> map, List<Fragment> keys) throws BlunderException {
        for (var key : keys) {
            map = map.remove(key);
        }

        return map;
    }
}
