package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.ImmutableItemBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClearSpellTrick extends Trick {
    public ClearSpellTrick() {
        super(Pattern.of(1, 4, 0, 3, 6, 4, 2, 5, 8, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var player = ctx.source().getPlayer();

        if (player.isEmpty()) {
            return BooleanFragment.FALSE;
        }

        return Optional.of(player.get().getOffHandStack()).map(stack -> {
            if (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
                var stacks = stack.get(DataComponentTypes.CONTAINER).stream().collect(Collectors.toCollection(ArrayList::new));
                var index = stack.get(ModComponents.SELECTED_SLOT).slot();

                var stack2 = stacks.get(index);
                if (stack2.contains(ModComponents.SPELL) && stack2.get(ModComponents.SPELL).immutable()) {
                    throw new ImmutableItemBlunder(this);
                }
                stack2.remove(ModComponents.SPELL);

                stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));
            } else {
                if (stack.contains(ModComponents.SPELL) && stack.get(ModComponents.SPELL).immutable()) {
                    throw new ImmutableItemBlunder(this);
                }
                stack.remove(ModComponents.SPELL);
            }
            return BooleanFragment.TRUE;
        }).orElse(BooleanFragment.FALSE);
    }
}
