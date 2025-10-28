package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SpellAccessory extends AccessoryItem {
    public SpellAccessory() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (reference.entity() instanceof PlayerEntity player && !player.getWorld().isClient()) {
            var caster = ModEntityComponents.CASTER.get(player);
            var fragment = stack.get(ModComponents.FRAGMENT);

            if (caster != null && fragment != null && fragment.value() instanceof SpellPart spell) {
                caster.setTormentSpell(spell);
            }
        }
    }
}
