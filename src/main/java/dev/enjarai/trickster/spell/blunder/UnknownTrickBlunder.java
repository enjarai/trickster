package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class UnknownTrickBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.literal("Unknown trick");
    }
}
