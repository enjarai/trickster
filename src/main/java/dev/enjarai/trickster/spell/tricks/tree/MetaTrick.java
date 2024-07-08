package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;

import java.util.Optional;

public abstract class MetaTrick extends Trick {
    public MetaTrick(Pattern pattern) {
        super(pattern);
    }

    protected Optional<SpellPart> findNode(SpellPart node, ListFragment addressFragment) {
        var address = addressFragment.sanitizeAddress(this);

        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index) {
                node = subParts.get(index);
            } else {
                // return empty if the spell does not contain a glyph at the address
                return Optional.empty();
            }
        }

        return Optional.of(node);
    }
}
