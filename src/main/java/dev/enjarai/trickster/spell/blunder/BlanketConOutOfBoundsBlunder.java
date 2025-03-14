package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BlanketConOutOfBoundsBlunder extends TrickBlunderException {
    private final Text cause;

    private BlanketConOutOfBoundsBlunder(Trick<?> source, Text explanation) {
        super(source);
        this.cause = explanation;
    }

    public BlanketConOutOfBoundsBlunder(Trick<?> source, BlockPos pos) {
        this(source, Text.literal("Block at " + pos.toShortString()));
    }

    public BlanketConOutOfBoundsBlunder(Trick<?> source, Entity entity) {
        this(source, Text.literal("Entity at " + entity.getX() + ", " + entity.getY() + ", " + entity.getZ()));
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(cause).append(" is beyond the permitted play area");
    }
}
