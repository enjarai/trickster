package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class ShadowingBuiltinTrickBlunder extends TrickBlunderException {
    private final Fragment value;

    public ShadowingBuiltinTrickBlunder(Trick source, PatternGlyph value) {
        super(source);
        this.value = value;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Attempted to shadow built in trick ").append(value.asText());
    }
}
