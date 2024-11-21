package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.Block;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BlockInvalidBlunder extends TrickBlunderException {
    private final Block block;

    public BlockInvalidBlunder(Trick source) {
        super(source);
        block = null;
    }

    public BlockInvalidBlunder(Trick source, Block block) {
        super(source);
        this.block = block;
    }

    @Override
    public MutableText createMessage() {
        if (block != null) {
            return super.createMessage().append("Invalid block type \"").append(Text.translatable(block.getTranslationKey())).append("\"");
        }

        return super.createMessage().append("Invalid block type");
    }
}
