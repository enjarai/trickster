package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.MacroComponent;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.util.Hamt;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReadMacroRing extends Trick {
    public ReadMacroRing() {
        super(Pattern.of(1, 2, 5, 8, 7, 6, 3, 0, 1, 5, 7, 3, 1, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return MacroComponent.getUserMergedMap(ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)))
            .map(ReadMacroRing::hamtAsMap)
            .map(Hamt::fromMap)
            .<Fragment>map(MapFragment::new)
            .orElse(VoidFragment.INSTANCE);
    }

    private static Map<PatternGlyph, SpellPart> hamtAsMap(Hamt<Pattern, SpellPart> hamt) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(hamt.iterator(), Spliterator.ORDERED), false)
                    .map(entry -> new AbstractMap.SimpleEntry<PatternGlyph, SpellPart>(new PatternGlyph(entry.getKey()), entry.getValue()))
                    .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
    }
}
