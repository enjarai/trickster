package dev.enjarai.trickster.spell.blunder;


import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ItemInvalidBlunder extends TrickBlunderException {
    public ItemInvalidBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.invalid_item"));
    }
}