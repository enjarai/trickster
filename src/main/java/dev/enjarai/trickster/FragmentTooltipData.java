package dev.enjarai.trickster;

import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.item.tooltip.TooltipData;

public record FragmentTooltipData(Fragment fragment) implements TooltipData {
    public FragmentTooltipData(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment contents() {
        return this.fragment;
    }
}
