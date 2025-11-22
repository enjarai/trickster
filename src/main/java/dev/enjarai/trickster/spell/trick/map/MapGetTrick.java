package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

import java.util.Optional;

public class MapGetTrick extends Trick<MapGetTrick> {
    public MapGetTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 6, 7), Signature.of(ArgType.ANY.mappedTo(ArgType.ANY), ArgType.ANY, MapGetTrick::run, RetType.ANY.optionalOfRet()));
    }

    public Optional<Fragment> run(SpellContext ctx, HashMap<Fragment, Fragment> map, Fragment key) {
        return map.get(key).toJavaOptional(); //TODO: consider blundering
    }
}
