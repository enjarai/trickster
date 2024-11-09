package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReadSpellTrick extends Trick {
    public ReadSpellTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5, 2, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {

        Function<SlotFragment, ItemStack> getRef = slotFragment -> slotFragment.reference(this, ctx);
        Optional<ItemStack> argument = supposeInput(fragments, FragmentType.SLOT, 0).map(getRef);
        Supplier<Optional<ItemStack>> offhand = () -> ctx.source().getOtherHandStack(stack -> stack.contains(ModComponents.FRAGMENT));

        return argument.or(offhand)
                .map(stack -> stack.get(ModComponents.FRAGMENT))
                .map(FragmentComponent::value)
                .orElse(VoidFragment.INSTANCE);
    }
}
