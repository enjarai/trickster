package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class RemoveSubtreeTrick extends AbstractMetaTrick<RemoveSubtreeTrick> {
    public RemoveSubtreeTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2, 4, 6, 7, 8), Signature.of(FragmentType.SPELL_PART, ADDRESS, RemoveSubtreeTrick::remove, FragmentType.SPELL_PART.optionalOfRet()));
    }

    public Optional<SpellPart> remove(SpellContext ctx, SpellPart spell, List<NumberFragment> address) {
        var newSpell = spell.deepClone();

        SpellPart prev = null;
        var node = newSpell;
        for (var index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index.asInt()) {
                var newNode = subParts.get(index.asInt());
                prev = node;
                node = newNode;
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        if (prev == null) {
            return Optional.empty();
        } else {
            prev.subParts.remove(address.getLast().asInt());
            return Optional.of(newSpell);
        }
    }
}
