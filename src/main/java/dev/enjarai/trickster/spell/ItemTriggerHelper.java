package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;

public class ItemTriggerHelper {
    public static void triggerMainHand(ServerPlayerEntity player, boolean isWeaponTrigger, Fragment... arguments) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();

        if (item instanceof ToolItem || (isWeaponTrigger && stack.isIn(ModItems.WEAPON_SPELL_TRIGGERS))) {
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
