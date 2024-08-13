package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class OutOfRangeBlunder extends TrickBlunderException {
    public final double maxRange;
    public final double usedRange;

    public OutOfRangeBlunder(Trick source, double maxRange, double usedRange) {
        super(source);
        this.maxRange = maxRange;
        this.usedRange = usedRange;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Range out of bounds: ").append(formatFloat((float) usedRange)).append(" is more than ").append(formatFloat((float) maxRange));
    }
}
