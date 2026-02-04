package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OverweightFragmentBlunder extends TrickBlunderException {
    public final Fragment found;
    public final int size;

    public OverweightFragmentBlunder(Trick<?> source, Fragment found, int size) {
        super(source);
        this.found = found;
        this.size = size;
    }

    @Override
    public MutableText createMessage() {
        return source.getName()
                .append(": ")
                .append(Text.translatable(Trickster.MOD_ID + ".blunder.overweight_fragment", found.asFormattedText(), size, Fragment.MAX_WEIGHT));
    }
}
