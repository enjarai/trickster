package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.vavr.collection.HashMap;

import java.util.Optional;

public class MapGetTrick extends Trick<MapGetTrick> {
    public MapGetTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 6, 7), Signature.of(map(Fragment.class, Fragment.class), ANY, MapGetTrick::run, RetType.ANY.maybe()));
    }

    public Optional<Fragment> run(SpellContext ctx, HashMap<Fragment, Fragment> map, Fragment key) throws BlunderException {
        return map.get(key).toJavaOptional(); //TODO: consider blundering
    }
}
