package dev.enjarai.trickster.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LevitatingBlockEntity extends Entity {
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    private BlockState blockState = Blocks.STONE.getDefaultState();
    @Nullable
    private NbtCompound blockEntityData;

    public LevitatingBlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.blockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("BlockState"));
        if (nbt.contains("BlockEntityData", NbtElement.COMPOUND_TYPE)) {
            this.blockEntityData = nbt.getCompound("BlockEntityData").copy();
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("BlockState", NbtHelper.fromBlockState(this.blockState));
        if (this.blockEntityData != null) {
            nbt.put("BlockEntityData", this.blockEntityData);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(this, entityTrackerEntry, Block.getRawIdFromState(blockState));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.blockState = Block.getStateFromRawId(packet.getEntityData());
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e, f);
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }
}
