package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncorrectFragmentBlunder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class RetrieveSubtreeTrick extends Trick {
    public RetrieveSubtreeTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8, 4, 0, 1, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        var address = addressFragment.sanitizeAddress(this);

        var node = spell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index && subParts.get(index).isPresent()) {
                node = subParts.get(index).get();
            } else {
                // return void if the spell does not contain a glyph at the address
                return VoidFragment.INSTANCE;
            }
        }

        return node;
    }
}
