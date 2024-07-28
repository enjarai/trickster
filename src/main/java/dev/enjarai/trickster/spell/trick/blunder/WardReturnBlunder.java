package dev.enjarai.trickster.spell.trick.blunder;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class WardReturnBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Ward handler return invalid");
    }
}
