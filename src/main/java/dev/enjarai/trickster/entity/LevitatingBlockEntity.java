package dev.enjarai.trickster.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class LevitatingBlockEntity extends Entity {
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Float> WEIGHT = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<NbtCompound> BLOCK_ENTITY_DATA = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    private BlockState blockState = Blocks.STONE.getDefaultState();

    public BlockEntity cachedBlockEntity;

    public LevitatingBlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
        builder.add(WEIGHT, 1f);
        builder.add(BLOCK_ENTITY_DATA, new NbtCompound());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.blockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("BlockState"));
//        if (nbt.contains("BlockEntityData", NbtElement.COMPOUND_TYPE)) {
//            this.blockEntityData = nbt.getCompound("BlockEntityData").copy();
//        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("BlockState", NbtHelper.fromBlockState(this.blockState));
//        if (this.blockEntityData != null) {
//            nbt.put("BlockEntityData", this.blockEntityData);
//        }
    }

    public static LevitatingBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state, float weight) {
        LevitatingBlockEntity fallingBlockEntity = new LevitatingBlockEntity(ModEntities.LEVITATING_BLOCK, world);
        fallingBlockEntity.setPosition(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5
        );
        fallingBlockEntity.intersectionChecked = true;
        fallingBlockEntity.setVelocity(Vec3d.ZERO);
        fallingBlockEntity.prevX = fallingBlockEntity.getX();
        fallingBlockEntity.prevY = fallingBlockEntity.getY();
        fallingBlockEntity.prevZ = fallingBlockEntity.getZ();
        fallingBlockEntity.blockState = state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, false) : state;
        fallingBlockEntity.setFallingBlockPos(pos);

        var entity = world.getBlockEntity(pos);
        if (entity != null) {
            fallingBlockEntity.setBlockEntityData(entity.createNbtWithId(world.getRegistryManager()));
            entity.read(new NbtCompound(), world.getRegistryManager());
        }

        fallingBlockEntity.setWeight(weight);

        world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
        world.spawnEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            this.applyGravity();
            this.move(MovementType.SELF, this.getVelocity());
            this.tickPortalTeleportation();

            if (getWeight() != 1.0f && !ModEntityComponents.GRACE.get(this).isInGrace("weight")) {
                if (getWeight() < 0.99f) {
                    setWeight(getWeight() + 0.01f);
                } else {
                    setWeight(1f);
                }
            }

            if (!this.getWorld().isClient) {
                BlockPos blockPos = this.getBlockPos();

                if (this.getWeight() >= 1 && isOnGround() && getWorld().getBlockState(blockPos).isReplaceable()) {
                    var isWater = getWorld().getFluidState(blockPos).isOf(Fluids.WATER);
                    var isWaterLoggable = this.blockState.contains(Properties.WATERLOGGED);

                    var blockState = isWater && isWaterLoggable ? this.blockState.with(Properties.WATERLOGGED, true) : this.blockState;

                    if (this.getWorld().setBlockState(blockPos, blockState, Block.NOTIFY_ALL)) {
                        ((ServerWorld)this.getWorld())
                                .getChunkManager()
                                .chunkLoadingManager
                                .sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.getWorld().getBlockState(blockPos)));
                        this.discard();

                        if (!this.getBlockEntityData().isEmpty() && blockState.hasBlockEntity()) {
                            BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                            if (blockEntity != null) {
                                NbtCompound nbtCompound = blockEntity.createNbt(this.getWorld().getRegistryManager());

                                for (String string : this.getBlockEntityData().getKeys()) {
                                    //noinspection DataFlowIssue
                                    nbtCompound.put(string, this.getBlockEntityData().get(string).copy());
                                }

                                try {
                                    blockEntity.read(nbtCompound, this.getWorld().getRegistryManager());
                                } catch (Exception var15) {
                                    Trickster.LOGGER.error("Failed to load block entity from levitating block", var15);
                                }

                                blockEntity.markDirty();
                            }
                        }
                    }
                }

                if (blockPos.getY() <= this.getWorld().getBottomY() || blockPos.getY() > this.getWorld().getTopY()) {
                    if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.dropItem(block);
                    }

                    this.discard();
                }
            }
        }
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

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected double getGravity() {
        return 0.04 * getWeight();
    }

    @Override
    public boolean isAttackable() {
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

    public void setWeight(float weight) {
        this.dataTracker.set(WEIGHT, weight);
    }

    public float getWeight() {
        return this.dataTracker.get(WEIGHT);
    }

    public void setBlockEntityData(NbtCompound compound) {
        this.dataTracker.set(BLOCK_ENTITY_DATA, compound);
    }

    public NbtCompound getBlockEntityData() {
        return this.dataTracker.get(BLOCK_ENTITY_DATA);
    }
}
