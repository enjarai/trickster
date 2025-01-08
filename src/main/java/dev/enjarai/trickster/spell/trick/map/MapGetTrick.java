package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class MapGetTrick extends Trick<MapGetTrick> {
    public MapGetTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 6, 7), Signature.of(FragmentType.MAP, ANY, MapGetTrick::run));
    }

    public Fragment run(SpellContext ctx, MapFragment map, Fragment key) throws BlunderException {
        return map.map().get(key).getOrElse(VoidFragment.INSTANCE); //TODO: consider blundering
    }
}
