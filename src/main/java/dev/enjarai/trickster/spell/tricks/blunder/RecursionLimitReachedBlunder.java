package dev.enjarai.trickster.spell.tricks.blunder;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class RecursionLimitReachedBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Recursion limit reached");
    }
}
