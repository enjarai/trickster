package dev.enjarai.trickster.spell.tricks.blunder;

import net.minecraft.text.MutableText;

public abstract class BlunderException extends RuntimeException {
    public abstract MutableText createMessage();
}
