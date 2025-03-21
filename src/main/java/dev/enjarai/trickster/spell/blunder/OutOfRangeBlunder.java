package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OutOfRangeBlunder extends TrickBlunderException {
    public final double maxRange;
    public final double usedRange;

    public OutOfRangeBlunder(Trick<?> source, double maxRange, double usedRange) {
        super(source);
        this.maxRange = maxRange;
        this.usedRange = usedRange;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.out_of_range", formatFloat((float) usedRange), formatFloat((float) maxRange)));
    }
}
