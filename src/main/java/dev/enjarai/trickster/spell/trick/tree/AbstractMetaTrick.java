package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMetaTrick<T extends AbstractMetaTrick<T>> extends DistortionTrick<T> {
    protected static final ArgType<List<NumberFragment>> ADDRESS = list(FragmentType.NUMBER);

    public AbstractMetaTrick(Pattern pattern) {
        super(pattern);
    }

    public AbstractMetaTrick(Pattern pattern, List<Signature<T>> handlers) {
        super(pattern, handlers);
    }

    public AbstractMetaTrick(Pattern pattern, Signature<T> primary) {
        super(pattern, primary);
    }

    protected Optional<SpellPart> findNode(SpellPart node, List<NumberFragment> address) {
        for (var index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index.asInt()) {
                node = subParts.get(index.asInt());
            } else {
                // return empty if the spell does not contain a glyph at the address
                return Optional.empty();
            }
        }

        return Optional.of(node);
    }
}
