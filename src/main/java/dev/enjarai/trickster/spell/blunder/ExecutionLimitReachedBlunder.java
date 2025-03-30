package dev.enjarai.trickster.spell.blunder;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ExecutionLimitReachedBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Execution limit reached");
    }
}
