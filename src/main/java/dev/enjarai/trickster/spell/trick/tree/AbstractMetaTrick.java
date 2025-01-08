package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMetaTrick<T extends AbstractMetaTrick<T>> extends DistortionTrick<T> {
    public AbstractMetaTrick(Pattern pattern) {
        super(pattern);
    }

    public AbstractMetaTrick(Pattern pattern, List<Signature<T>> handlers) {
        super(pattern, handlers);
    }

    public AbstractMetaTrick(Pattern pattern, Signature<T> primary) {
        super(pattern, primary);
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
