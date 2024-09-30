package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class WardReturnBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Ward handler return invalid");
    }
}
