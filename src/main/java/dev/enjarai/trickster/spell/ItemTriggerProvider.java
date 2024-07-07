package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
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
        var stack = player.getAllArmorItems().iterator().next();

        if (stack.getItem() instanceof ArmorItem) {
            trickster$trigger(player, stack, List.of(arguments));
        }
    }

    default void trickster$trigger(ServerPlayerEntity player, ItemStack stack, List<Fragment> arguments) {
        var spellComponent = stack.get(ModComponents.SPELL);

        if (spellComponent != null) {
            var ctx = new PlayerSpellContext(player, EquipmentSlot.MAINHAND);
            ctx.pushPartGlyph(arguments);
            spellComponent.spell().run(ctx);
            ctx.popPartGlyph();
        }
    }
}
