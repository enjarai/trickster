package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.pond.FuelableFurnaceDuck;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    @Unique
    boolean superHeated;

    @Override
    public void trickster$setFuelLevelAndSuperHeat(int level) {
        burnTime = level;
        fuelTime = level;
        superHeated = true;

        var state = getCachedState().with(AbstractFurnaceBlock.LIT, isBurning());
        //noinspection DataFlowIssue
        world.setBlockState(pos, state, 3);

        markDirty();
    }

    @Inject(
            method = "writeNbt",
            at = @At("TAIL")
    )
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        nbt.putBoolean("trickster:SuperHeated", superHeated);
    }

    @Inject(
            method = "readNbt",
            at = @At("TAIL")
    )
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        superHeated = nbt.getBoolean("trickster:SuperHeated");
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            )
    )
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        ((AbstractFurnaceBlockEntityMixin) (Object) blockEntity).tick();
    }

    @Unique
    void tick() {
        if (!isBurning() && superHeated) {
            superHeated = false;
            markDirty();
        }
    }
}
