package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.MacroComponent;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public class ReadMacroRing extends Trick {
    public ReadMacroRing() {
        super(Pattern.of(1, 2, 5, 8, 7, 6, 3, 0, 1, 5, 7, 3, 1, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));

        Fragment mapFragmemnt = VoidFragment.INSTANCE;

        ItemStack ring = SlotReference.of(player, "ring", 0).getStack();
        var mapComponent = MacroComponent.getMap(ring);

        if (ring != null && mapComponent.isPresent()) {
            mapFragmemnt = convertToMapFragment(mapComponent.get());
        }

        return mapFragmemnt;
    }

    private MapFragment convertToMapFragment(Hamt<Pattern, SpellPart> map) {
        var macros = new HashMap<Fragment, Fragment>();

        map.iterator().forEachRemaining(entry ->
                macros.put(new PatternGlyph(entry.getKey()), entry.getValue())
        );

        return new MapFragment(Hamt.fromMap(macros));
    }
}
