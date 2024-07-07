package dev.enjarai.trickster;

import dev.enjarai.trickster.spell.SpellPart;

import net.minecraft.item.tooltip.TooltipData;

public record SpellTooltipData(SpellPart spell) implements TooltipData {
    public SpellTooltipData(SpellPart spell) {
        this.spell = spell;
    }

    public SpellPart contents() {
        return this.spell;
    }
}
