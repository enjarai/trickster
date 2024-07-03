package dev.enjarai.trickster.block;

import dev.enjarai.trickster.cca.ModBlockComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ShadowBlockEntity extends BlockEntity {
    public ShadowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SHADOW_ENTITY, pos, state);
    }

    public Block disguise() {
        return ModBlockComponents.SHADOW_DISGUISE.get(this).value();
    }

    public void disguise(Block block) {
        ModBlockComponents.SHADOW_DISGUISE.get(this).value(block);
    }
}
