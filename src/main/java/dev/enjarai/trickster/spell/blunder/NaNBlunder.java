package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NaNBlunder extends BlunderException {
    @Override
    public MutableText createMessage() {
        return Text.translatable(Trickster.MOD_ID + ".blunder.nan");
    }
}
