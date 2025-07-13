package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

public class MapCreateTrick extends Trick<MapCreateTrick> {
    public MapCreateTrick() {
        super(Pattern.of(2, 5, 8, 6, 3, 0), Signature.of(MapCreateTrick::create, RetType.ANY.mappedTo(RetType.ANY)));
    }

    public HashMap<Fragment, Fragment> create(SpellContext ctx) throws BlunderException {
        return HashMap.empty();
    }
}
