package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class AtomicChunkTooLargeBlunder extends TrickBlunderException {
    public AtomicChunkTooLargeBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return Text.translatable(Trickster.MOD_ID + ".blunder.atomic_chunk_too_large");
    }
}
