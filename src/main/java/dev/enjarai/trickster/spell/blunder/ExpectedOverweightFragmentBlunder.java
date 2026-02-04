package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ExpectedOverweightFragmentBlunder extends TrickBlunderException {
    public final int weight;

    public ExpectedOverweightFragmentBlunder(Trick<?> source, int weight) {
        super(source);
        this.weight = weight;
    }

    @Override
    public MutableText createMessage() {
        return source.getName()
                .append(": ")
                .append(Text.translatable(Trickster.MOD_ID + ".blunder.expected_overweight_fragment", weight, Fragment.MAX_WEIGHT));
    }
}
