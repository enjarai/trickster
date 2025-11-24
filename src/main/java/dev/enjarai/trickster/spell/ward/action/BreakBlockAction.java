package dev.enjarai.trickster.spell.ward.action;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakBlockAction implements Action<Target.Block> {
    private final BlockPos pos;

    public BreakBlockAction(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public ActionType<?> type() {
        return ActionType.BREAK_BLOCK;
    }

    @Override
    public Target.Block target(World world) {
        return new Target.Block(pos);
    }

    @Override
    public float cost(World world) {
        return Math.max(world.getBlockState(pos).getBlock().getHardness(), 8);
    }
}
