package dev.enjarai.trickster.spell.trick.map;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class MapMergeTrick extends DistortionTrick<MapMergeTrick> {
    public MapMergeTrick() {
        super(Pattern.of(2, 4, 8, 2, 5, 8, 6, 3, 0), Signature.of(FragmentType.MAP, variadic(FragmentType.MAP), MapMergeTrick::run));
    }

    public Fragment run(SpellContext ctx, MapFragment baseMap, List<MapFragment> maps) throws BlunderException {
        for (var map : maps) {
            baseMap = baseMap.mergeWith(map);
        }

        return baseMap;
    }
}
