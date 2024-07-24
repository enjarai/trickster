package dev.enjarai.trickster.spell.tricks.blunder;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class SlowWardBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Ward handler did not complete in less than one tick");
    }
}
