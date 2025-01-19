package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CantEditBlockBlunder extends TrickBlunderException {
    public final BlockPos pos;

    public CantEditBlockBlunder(Trick<?> source, BlockPos pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.cannot_edit_block", pos.toShortString()));
    }
}
