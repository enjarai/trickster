package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.ImmutableItemBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WriteSpellTrick extends Trick {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, FragmentType.SPELL_PART, 0);

        return ctx.getOtherHandSpellStack().map(stack -> {
            var newSpell = spell.deepClone();
            newSpell.brutallyMurderEphemerals();

            if (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
                var stacks = stack.get(DataComponentTypes.CONTAINER).stream().collect(Collectors.toCollection(ArrayList::new));
                var index = stack.get(ModComponents.SELECTED_SLOT).slot();

                var stack2 = stacks.get(index);
                if (stack2.contains(ModComponents.SPELL) && stack2.get(ModComponents.SPELL).immutable()) {
                    throw new ImmutableItemBlunder(this);
                }
                stack2.set(ModComponents.SPELL, new SpellComponent(newSpell));

                stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));
            } else {
                if (stack.contains(ModComponents.SPELL) && stack.get(ModComponents.SPELL).immutable()) {
                    throw new ImmutableItemBlunder(this);
                }
                stack.set(ModComponents.SPELL, new SpellComponent(newSpell));
            }
            return BooleanFragment.TRUE;
        }).orElse(BooleanFragment.FALSE);
    }
}
