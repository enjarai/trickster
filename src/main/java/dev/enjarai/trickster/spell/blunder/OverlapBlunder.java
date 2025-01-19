package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.overlapping_positions", pos1.asFormattedText(), pos2.asFormattedText()));
    }
}