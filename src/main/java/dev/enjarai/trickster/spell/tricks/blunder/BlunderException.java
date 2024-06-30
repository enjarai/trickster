package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class BlunderException extends RuntimeException {
    public abstract MutableText createMessage();

    protected Text formatInt(int number) {
        return Text.literal("" + number).withColor(FragmentType.NUMBER.color().getAsInt());
    }
}
