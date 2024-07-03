package dev.enjarai.trickster.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ShadowBlockEntity extends BlockEntity {
    public Block disguise = Blocks.BEACON; //TODO

    public ShadowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SHADOW_ENTITY, pos, state);
    }

    public ShadowBlockEntity(BlockPos pos, BlockState state, Block disguise) {
        this(pos, state);
        this.disguise = disguise;
    }
}
