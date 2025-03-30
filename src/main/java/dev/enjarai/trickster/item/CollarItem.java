package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.accessories.api.AccessoryItem;
import net.minecraft.entity.Entity;
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

    public static void playJingleQuestionMark(LivingEntity entity, boolean server) {
        if (entity.accessoriesCapability() != null && entity.accessoriesCapability().isEquipped(ModItems.COLLAR)) {
            entity.getWorld().playSoundFromEntity(
                    !server && entity instanceof PlayerEntity player ? player : null, entity, ModSounds.COLLAR_BELL,
                    entity.getSoundCategory(), 0.18f, 1f
            );
        }
    }
}
