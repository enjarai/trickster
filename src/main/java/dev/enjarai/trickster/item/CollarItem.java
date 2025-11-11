package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class CollarItem extends AccessoryItem {
    public CollarItem(Settings properties) {
        super(properties.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (!stack.contains(ModComponents.COLLAR_LINK)) {
            tooltip.add(Text.translatable("trickster.tooltip.unlinked").withColor(0x775577));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        var capability = reference.capability();

        if (capability == null) {
            return false;
        }

        int amount = capability.getEquipped(s -> s.getItem() instanceof CollarItem).size();

        if (amount == 0) {
            return true;
        }

        if (amount == 1 && reference.getStack().getItem() instanceof CollarItem) {
            return true;
        }

        return false;
    }

    public static void playJingleQuestionMark(LivingEntity entity, boolean server) {
        //noinspection DataFlowIssue
        if (entity.accessoriesCapability() != null && entity.accessoriesCapability().isEquipped(ModItems.COLLAR)) {
            entity.getWorld().playSoundFromEntity(
                    !server && entity instanceof PlayerEntity player ? player : null, entity, ModSounds.COLLAR_BELL,
                    entity.getSoundCategory(), 0.18f, 1f
            );
        }
    }
}
