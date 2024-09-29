package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.Optional;

public abstract class AbstractMetaTrick extends Trick {
    public AbstractMetaTrick(Pattern pattern) {
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
