package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;

public interface ItemTriggerProvider {
    default void trickster$triggerMainHand(ServerPlayerEntity player, Fragment... arguments) {
        var spellComponent = player.getMainHandStack().get(ModComponents.SPELL);

        if (spellComponent != null) {
            var ctx = new PlayerSpellContext(player, EquipmentSlot.MAINHAND);
            ctx.pushPartGlyph(Arrays.stream(arguments).toList());
            spellComponent.spell().run(ctx);
            ctx.popPartGlyph();
        }
    }
}
