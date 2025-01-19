package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BlockUnoccupiedBlunder extends TrickBlunderException {
    public final VectorFragment pos;

    public BlockUnoccupiedBlunder(Trick<?> source, VectorFragment pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.block_unoccupied", pos.asFormattedText()));
    }
}
