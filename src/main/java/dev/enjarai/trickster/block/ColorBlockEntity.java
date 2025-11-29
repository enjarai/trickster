package dev.enjarai.trickster.block;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ColorBlockEntity extends BlockEntity implements SpellColoredBlockEntity, RenderDataBlockEntity {

    public int[] colors = new int[] { DyeColor.CYAN.getEntityColor() };

    public ColorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.COLOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int[] getColors() {
        return colors;
    }

    @Override
    public void setColors(int[] colors) {
        this.colors = colors;
        markDirty();
        if (world == null) return;
        world.setBlockState(getPos(), world.getBlockState(getPos()).with(ColorBlock.TRANSLUCENT, (colors[0] & 0xFF000000) != 0));
        world.updateListeners(pos, getCachedState(), getCachedState(), 0);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putIntArray("colors", colors);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        colors = nbt.getIntArray("colors");
        if (colors.length == 0) {
            colors = new int[] { 0xffffff };
        }
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public @Nullable Object getRenderData() {
        return colors[0];
    }
}
