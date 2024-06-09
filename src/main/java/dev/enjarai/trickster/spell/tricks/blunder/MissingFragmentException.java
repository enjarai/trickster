package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.Text;

public class MissingFragmentException extends BlunderException {
    public final int index;
    public final FragmentType<?> expectedType;

    public MissingFragmentException(Trick source, int index, FragmentType<?> expectedType) {
        super(source);
        this.index = index;
        this.expectedType = expectedType;
    }

    @Override
    public Text createMessage() {
        return source.getName().append(": ").append(Text.translatable(Trickster.MOD_ID + ".blunder.missing_fragment", index, expectedType.getName()));
    }
}
