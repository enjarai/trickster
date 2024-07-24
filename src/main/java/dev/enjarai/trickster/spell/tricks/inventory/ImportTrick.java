package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.tricks.func.ForkingTrick;

import java.util.List;

public class ImportTrick extends Trick implements ForkingTrick {
    public ImportTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return null;
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var itemType = expectInput(fragments, FragmentType.ITEM_TYPE, 0);

        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (stack.isOf(itemType.item())) {
                var component = stack.get(ModComponents.SPELL);

                if (component != null)
                    return new SpellExecutor(component.spell(), fragments.subList(1, fragments.size()));
            }
        }

        throw new MissingItemBlunder(this);
    }
}
