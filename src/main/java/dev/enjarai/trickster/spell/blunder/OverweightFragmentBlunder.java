package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OverweightFragmentBlunder extends TrickBlunderException {
    public final Fragment found;

    public OverweightFragmentBlunder(Trick source, Fragment found) {
        super(source);
        this.found = found;
    }

    @Override
    public MutableText createMessage() {
        return source.getName().append(": ").append(Text.translatable(
                Trickster.MOD_ID + ".blunder.overweight_fragment", found.asFormattedText()));
    }
}
