package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;

public class ItemTriggerHelper {
    public static void triggerMainHand(ServerPlayerEntity player, Fragment... arguments) {
        var stack = player.getMainHandStack();

        if (stack.getItem() instanceof ToolItem) {
            trigger(player, stack, List.of(arguments));
        }
    }

    public static void triggerBoots(ServerPlayerEntity player, Fragment... arguments) {
        triggerArmor(player, 0, arguments);
    }

    public static void triggerArmor(ServerPlayerEntity player, int index, Fragment... arguments) {
        int i = 0;

        for (var item : player.getAllArmorItems()) {
            if (index == i) {
                trigger(player, item, List.of(arguments));
                return;
            }

            i++;
        }
    }

    public static void trigger(ServerPlayerEntity player, ItemStack stack, List<Fragment> arguments) {
        var spellComponent = stack.get(ModComponents.SPELL);

        if (spellComponent != null) {
            ModEntityCumponents.CASTER.get(player).queueSpellAndCast(spellComponent.spell(), arguments, Optional.empty());
        }
    }
}
