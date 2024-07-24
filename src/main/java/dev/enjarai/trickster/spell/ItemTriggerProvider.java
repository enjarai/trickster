package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public interface ItemTriggerProvider {
    default void trickster$triggerMainHand(ServerPlayerEntity player, Fragment... arguments) {
        var stack = player.getMainHandStack();

        if (stack.getItem() instanceof ToolItem) {
            trickster$trigger(player, stack, List.of(arguments));
        }
    }

    default void trickster$triggerBoots(ServerPlayerEntity player, Fragment... arguments) {
        trickster$triggerArmour(player, 0, arguments);
    }

    default void trickster$triggerArmour(ServerPlayerEntity player, int index, Fragment... arguments) {
        int i = 0;

        for (var item : player.getAllArmorItems()) {
            if (index == i) {
                trickster$trigger(player, item, List.of(arguments));
                return;
            }

            i++;
        }
    }

    default void trickster$trigger(ServerPlayerEntity player, ItemStack stack, List<Fragment> arguments) {
        var spellComponent = stack.get(ModComponents.SPELL);

        if (spellComponent != null) {
            new SpellExecutor(spellComponent.spell(), arguments).run(new PlayerSpellSource(player));
        }
    }
}
