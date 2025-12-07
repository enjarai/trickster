package dev.enjarai.trickster.spell.ward.action;

import dev.enjarai.trickster.spell.Source;
import net.minecraft.util.math.BlockPos;

public class BreakBlockAction extends Action<Target.Block> {
    private final BlockPos pos;

    public BreakBlockAction(Source source, BlockPos pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public ActionType<?> type() {
        return ActionType.BREAK_BLOCK;
    }

    @Override
    public Target.Block target() {
        return new Target.Block(pos);
    }

    @Override
    public float cost() {
        return Math.max(source.getWorld().getBlockState(pos).getBlock().getHardness(), 8);
    }
}
