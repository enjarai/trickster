package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;

import java.util.List;

//TODO: distortion?
public class MapInsertTrick extends Trick<MapInsertTrick> {
    public MapInsertTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 8),
                Signature.of(ArgType.ANY.mappedTo(ArgType.ANY), ArgType.ANY.pairedWith(ArgType.ANY).variadicOfArg().require(), MapInsertTrick::run, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public HashMap<Fragment, Fragment> run(SpellContext ctx, HashMap<Fragment, Fragment> map, List<Tuple2<Fragment, Fragment>> pairs) {
        for (var pair : pairs) {
            map = map.put(pair._1(), pair._2());
        }

        return map;
    }
}
