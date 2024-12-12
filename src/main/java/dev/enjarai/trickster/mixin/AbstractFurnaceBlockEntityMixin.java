package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.pond.FuelableFurnaceDuck;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends LockableContainerBlockEntity implements FuelableFurnaceDuck {

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow
    protected abstract boolean isBurning();

    @Shadow
    int fuelTime;
    @Shadow
    int burnTime;

    @Override
    public void trickster$setFuelLevelAtLeast(int level) {
        if (burnTime < level) {
            burnTime = level;
            fuelTime = level;

            var state = getCachedState().with(AbstractFurnaceBlock.LIT, isBurning());
            //noinspection DataFlowIssue
            world.setBlockState(pos, state, 3);

            markDirty();
        }
    }
}
