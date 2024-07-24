package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.*;

public class RetrieveGlyphTrick extends MetaTrick {
    public RetrieveGlyphTrick() {
        super(Pattern.of(2, 1, 0, 4, 6, 7, 8));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        return findNode(spell, addressFragment).map(node -> node.glyph).orElse(VoidFragment.INSTANCE);
    }
}
