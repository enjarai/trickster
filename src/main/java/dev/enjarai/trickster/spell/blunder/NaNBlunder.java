package dev.enjarai.trickster.spell.blunder;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NaNBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Number must be a number");
    }
}
