package dev.enjarai.trickster.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public class EntangledRedstoneBlock extends Block {
    public EntangledRedstoneBlock() {
        super(Blocks.REDSTONE_BLOCK.getSettings());
        setDefaultState(getDefaultState().with(Properties.POWER, 15));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWER);
    }
}
