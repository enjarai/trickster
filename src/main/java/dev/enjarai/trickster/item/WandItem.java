package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WandItem extends Item implements LeftClickItem {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return use(world, user, hand, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand, Boolean rightClick) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            var component = stack.get(ModComponents.FRAGMENT);
            if (component != null) {
                var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
                ModEntityComponents.CASTER.get(user).queueSpell(spell, List.of(BooleanFragment.of(rightClick)));
            }
        }

        return TypedActionResult.success(stack);
    }
}
