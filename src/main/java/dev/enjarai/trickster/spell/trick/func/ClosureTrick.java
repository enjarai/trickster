package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import io.vavr.collection.Map;

public class ClosureTrick extends DistortionTrick<ClosureTrick> {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1), Signature.of(FragmentType.SPELL_PART, FragmentType.MAP, ClosureTrick::passedMap));
    }

    public Fragment passedMap(SpellContext ctx, SpellPart spell, MapFragment map) throws BlunderException {
        return run(ctx, spell, map.map());
    }

    public Fragment run(SpellContext ctx, SpellPart spell, Map<Fragment, Fragment> map) throws BlunderException {
        return spell.deepClone().buildClosure(map);
    }
}
