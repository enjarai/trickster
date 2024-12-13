package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class WriteAddressToOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 4, 8, 7, 6, 4, 2, 1, 4);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        var address = getAddress(root, drawingPart);

        if (address.isPresent()) {
            var addressFragment = new ListFragment(address.get().stream().map(num -> (Fragment) new NumberFragment(num)).toList());
            ctx.updateOtherHandSpell(new SpellPart(addressFragment, List.of()));
        }

        return root;
    }

    private static Optional<List<Integer>> getAddress(SpellPart node, SpellPart target) {
        var address = new LinkedList<Integer>();
        var found = getAddress(node, target, address, new LinkedList<>());

        if (found) {
            return Optional.of(address);
        } else {
            return Optional.empty();
        }
    }

    private static boolean getAddress(SpellPart node, SpellPart target, List<Integer> address, List<SpellPart> glyphSpells) {
        if (node == target) {
            return true;
        }

        if (node.glyph instanceof SpellPart glyph) {
            glyphSpells.add(glyph);
        }

        var subParts = node.subParts;

        for (int i = 0; i < subParts.size(); i++) {
            address.add(i);

            var found = getAddress(subParts.get(i), target, address, glyphSpells);
            if (found)
                return true;

            address.removeLast();
        }

        if (address.isEmpty()) {
            for (var glyph : glyphSpells) {
                var found = getAddress(glyph, target, address, new LinkedList<>());
                if (found)
                    return true;
            }
        }

        return false;
    }
}
