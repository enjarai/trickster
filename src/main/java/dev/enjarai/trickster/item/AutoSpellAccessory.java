package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

public class AutoSpellAccessory extends AccessoryItem {
    private int tickCount = 0;

    public AutoSpellAccessory() {
        super(new Settings()
                .maxCount(1));
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if ((LivingEntity) (Object) reference.entity() instanceof ServerPlayerEntity player) {
            tickCount %= 20;
            if (tickCount == 0) {
                ItemTriggerHelper.trigger(player, stack, new ArrayList<Fragment>());
            }
            tickCount += 1;
        }
    }
}
