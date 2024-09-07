package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.MacroComponent;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public class AddMacroTrick extends Trick {
    public AddMacroTrick() {
        super(Pattern.of(1, 2, 5, 8, 7, 6, 3, 0, 1, 5, 7, 3, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));

        var mapFragmemnt = expectInput(fragments, FragmentType.MAP, 0);

        ItemStack ring = SlotReference.of(player, "ring", 0).getStack();
        var mapComponent = MacroComponent.getMap(ring);

        if (ring != null && mapComponent.isPresent()) {
            MacroComponent.setMap(ring, expectMacroMap(mapFragmemnt.map()));

            return BooleanFragment.TRUE;
        } else {
            return BooleanFragment.FALSE;
        }
    }

    private Hamt<Pattern, SpellPart> expectMacroMap(Hamt<Fragment, Fragment> map) {
        var macros = new HashMap<Pattern, SpellPart>();

        map.iterator().forEachRemaining(entry -> {
            if (entry.getKey() instanceof SpellPart spellKey && spellKey.glyph instanceof PatternGlyph pattern
                    && entry.getValue() instanceof SpellPart spell) {
                macros.put(pattern.pattern(), spell);
            } else {
                throw new IncorrectFragmentBlunder(this, 0,
                        FragmentType.MAP.getName()
                                .append("<")
                                .append(FragmentType.PATTERN.getName())
                                .append(", ")
                                .append(FragmentType.SPELL_PART.getName())
                                .append(">"),
                        new MapFragment(map));
            }
        });

        return Hamt.fromMap(macros);
    }
}