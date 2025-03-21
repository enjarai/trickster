package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.Block;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CannotPlaceBlockBlunder extends TrickBlunderException {
    public final Block block;
    public final VectorFragment pos;

    public CannotPlaceBlockBlunder(Trick<?> source, Block block, VectorFragment pos) {
        super(source);
        this.block = block;
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.cannot_place_block", block.getName(), pos.asFormattedText()));
    }
}
