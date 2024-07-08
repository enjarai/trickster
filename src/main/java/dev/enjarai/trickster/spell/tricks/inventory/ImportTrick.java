package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class ImportTrick extends Trick {
    public ImportTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var itemType = expectInput(fragments, FragmentType.ITEM_TYPE, 0);

        var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (stack.isOf(itemType.item())) {
                var component = stack.get(ModComponents.SPELL);

                if (component != null) {
                    ctx.pushPartGlyph(List.of());
                    ctx.pushStackTrace(-2);
                    var result = component.spell().run(ctx);
                    ctx.popStackTrace();
                    ctx.popPartGlyph();
                    return result;
                }
            }
        }

        return VoidFragment.INSTANCE;
    }
}
