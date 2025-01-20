package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.ModComponents;
import io.wispforest.accessories.api.AccessoryItem;
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
}
