package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.component.DataComponentTypes;

import java.util.List;
import java.util.Optional;

public class ReadSpellTrick extends Trick {
    public ReadSpellTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5, 2, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.getOtherHandSpellStack()
                .filter(stack -> stack.contains(ModComponents.SPELL) || (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)))
                .flatMap(stack -> {
                    if (stack.isIn(ModItems.HOLDABLE_HAT)) {
                        return stack.get(DataComponentTypes.CONTAINER).stream()
                                .skip(stack.get(ModComponents.SELECTED_SLOT).slot())
                                .findFirst()
                                .filter(stack2 -> stack2.contains(ModComponents.SPELL));
                    }
                    return Optional.of(stack);
                })
                .<Fragment>map(stack -> stack.get(ModComponents.SPELL).spell())
                .orElse(VoidFragment.INSTANCE);
    }
}
