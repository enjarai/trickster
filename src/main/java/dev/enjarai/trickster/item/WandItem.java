package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            var component = stack.get(ModComponents.FRAGMENT);
            if (component != null) {
                var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
                ModEntityComponents.CASTER.get(user).queueSpell(spell, List.of());
            }
        }

        return ActionResult.SUCCESS;
    }
}
