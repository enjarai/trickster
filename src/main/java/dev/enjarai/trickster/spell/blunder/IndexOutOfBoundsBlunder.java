package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class IndexOutOfBoundsBlunder extends TrickBlunderException {
    public final int index;

    public IndexOutOfBoundsBlunder(Trick<?> source, int index) {
        super(source);
        this.index = index;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.index_out_of_bounds", formatInt(index)));
    }
}
