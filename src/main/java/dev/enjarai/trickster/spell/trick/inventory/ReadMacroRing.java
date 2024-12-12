package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;

import java.util.List;

public class ReadMacroRing extends Trick<ReadMacroRing> {
    public ReadMacroRing() {
        super(Pattern.of(1, 2, 5, 8, 7, 6, 3, 0, 1, 5, 7, 3, 1, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return FragmentComponent.getUserMergedMap(ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)), "ring")
            .map(ReadMacroRing::collectMap)
            .<Fragment>map(MapFragment::new)
            .orElse(VoidFragment.INSTANCE);
    }

    private static HashMap<Fragment, Fragment> collectMap(HashMap<Pattern, SpellPart> hamt) {
        return hamt.map((k, v) -> new Tuple2<>(new PatternGlyph(k), v));
    }
}
