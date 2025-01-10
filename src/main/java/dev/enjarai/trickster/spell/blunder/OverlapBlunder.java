package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class OverlapBlunder extends TrickBlunderException {
    public final VectorFragment pos1;
    public final VectorFragment pos2;

    public OverlapBlunder(Trick<?> source, VectorFragment pos1, VectorFragment pos2) {
        super(source);
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Overlapping positions: ")
                .append(pos1.asFormattedText()).append(", ").append(pos2.asFormattedText());
    }
}
