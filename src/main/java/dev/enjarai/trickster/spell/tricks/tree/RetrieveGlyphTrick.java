package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.*;

public class RetrieveGlyphTrick extends Trick {
    public RetrieveGlyphTrick() {
        super(Pattern.of(2, 1, 0, 4, 6, 7, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        var address = addressFragment.sanitizeAddress(this);

        var node = spell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index) {
                node = subParts.get(index);
            } else {
                // return void if the spell does not contain a glyph at the address
                return VoidFragment.INSTANCE;
            }
        }

        return node.glyph;
    }

}
