package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.Text;

public class IncorrectFragmentException extends BlunderException {
    public final int index;
    public final FragmentType<?> expectedType;
    public final FragmentType<?> foundType;

    public IncorrectFragmentException(Trick source, int index, FragmentType<?> expectedType, FragmentType<?> foundType) {
        super(source);
        this.index = index;
        this.expectedType = expectedType;
        this.foundType = foundType;
    }

    @Override
    public Text createMessage() {
        return source.getName().append(": ").append(Text.translatable(Trickster.MOD_ID + ".blunder.incorrect_fragment", index, expectedType.getName(), foundType.getName()));
    }
}
