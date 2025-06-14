package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.Map;

public class ClosureTrick extends DistortionTrick<ClosureTrick> {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1), Signature.of(FragmentType.SPELL_PART, FragmentType.MAP, ClosureTrick::passedMap, FragmentType.SPELL_PART));
    }

    public SpellPart passedMap(SpellContext ctx, SpellPart spell, MapFragment map) throws BlunderException {
        return run(ctx, spell, map.map());
    }

    public SpellPart run(SpellContext ctx, SpellPart spell, Map<Fragment, Fragment> map) throws BlunderException {
        return spell.deepClone().buildClosure(map);
    }
}
