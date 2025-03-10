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
import net.minecraft.entity.projectile.ProjectileUtil;
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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LevitatingBlockEntity extends Entity {
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Float> WEIGHT = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<NbtCompound> BLOCK_ENTITY_DATA = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    protected static final TrackedData<Boolean> SHOULD_REVERT_NOW = DataTracker.registerData(LevitatingBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private BlockState blockState = Blocks.STONE.getDefaultState();

    public BlockEntity cachedBlockEntity;

    public int onGroundTicks = 0;

    public LevitatingBlockEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
        builder.add(WEIGHT, 1f);
        builder.add(BLOCK_ENTITY_DATA, new NbtCompound());
        builder.add(SHOULD_REVERT_NOW, false);
    }

    @Override
    public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> entries) {
        super.onDataTrackerUpdate(entries);
        this.intersectionChecked = true;
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
            this.applyGravity();
            this.move(MovementType.SELF, this.getVelocity());
            this.tickPortalTeleportation();

            if (this.getWorld().isClient()) {
                if (this.isOnGround() && this.getVelocity().lengthSquared() > 0.3 * 0.3) {
                    for (int i = 0; i < 10; i++) {
                        this.spawnSprintingParticles();
                    }

                    BlockPos blockPos = this.getStepSoundPos(this.getSteppingPos());
                    BlockState blockState = this.getWorld().getBlockState(blockPos);
                    BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                    this.getWorld().playSound(
                            getX(), getY(), getZ(), blockSoundGroup.getStepSound(), SoundCategory.BLOCKS,
                            blockSoundGroup.getVolume(), blockSoundGroup.getPitch(), true //  * 0.15F
                    );
                }

                if (this.isOnGround()) {
                    this.onGroundTicks++;
                } else {
                    this.onGroundTicks = 0;
                }
            }

            if (getWeight() != 1.0f && !ModEntityComponents.GRACE.get(this).isInGrace("weight")) {
                if (getWeight() < 0.99f) {
                    setWeight(getWeight() + 0.01f);
                } else {
                    setWeight(1f);
                }
            }

            this.tickCollisions();
            this.trySolidify();

            if (!this.getWorld().isClient() && getBlockPos().getY() <= this.getWorld().getBottomY() - 64) {
                this.discard();
            }
        }
    }

    protected void trySolidify() {
        if (this.getWeight() >= 1 && (isOnGround() || getShouldRevertNow()) && supportingBlockPos.isPresent() &&
                this.getVelocity().lengthSquared() < 0.2 * 0.2) {
            var targetPos = getBlockY() > supportingBlockPos.get().getY() ? supportingBlockPos.get().up() : supportingBlockPos.get();

            if (getWorld().getBlockState(targetPos).isReplaceable()) {
                // At this point we start solidifying

                if (this.getPos().squaredDistanceTo(targetPos.toBottomCenterPos()) < 0.05 * 0.05) {
                    // If close enough to target position, solidify fully
                    if (!this.getWorld().isClient()) {
                        var isWater = getWorld().getFluidState(targetPos).isOf(Fluids.WATER);
                        var isWaterLoggable = this.blockState.contains(Properties.WATERLOGGED);

                        var blockState = isWater && isWaterLoggable ? this.blockState.with(Properties.WATERLOGGED, true) : this.blockState;

                        if (this.getWorld().setBlockState(targetPos, blockState, Block.NOTIFY_ALL)) {
                            ((ServerWorld) this.getWorld())
                                    .getChunkManager()
                                    .chunkLoadingManager
                                    .sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(targetPos, this.getWorld().getBlockState(targetPos)));
                            this.discard();

                            if (!this.getBlockEntityData().isEmpty() && blockState.hasBlockEntity()) {
                                BlockEntity blockEntity = this.getWorld().getBlockEntity(targetPos);
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
                } else {
                    // If not close enough, start moving towards the target
                    var offset = targetPos.toBottomCenterPos().subtract(this.getPos());
                    var distance = offset.length();
                    var travelDistance = offset.normalize().multiply(Math.min(distance, 0.2));

                    this.setPosition(this.getPos().add(travelDistance));
                }
            }
        }
    }

    @Override
    protected float getVelocityMultiplier() {
        if (isOnGround()) {
            return super.getVelocityMultiplier() * 0.9f;
        }
        return super.getVelocityMultiplier();
    }

    public void tickCollisions() {
        var velocity = this.getVelocity();
        var currentPos = this.getPos();
        var nextPos = currentPos.add(velocity);

        var hit = getEntityCollision(currentPos, nextPos);
        if (hit != null) {
            hit.getEntity().damage(getWorld().getDamageSources().fallingBlock(this),
                    (float) hit.getEntity().getVelocity().add(velocity.negate()).length() * blockState.getHardness(getWorld(), getFallingBlockPos()));

            hit.getEntity().setVelocity(hit.getEntity().getVelocity().add(velocity));
            this.setVelocity(velocity.multiply(0.2));
        }
    }

    protected boolean canHit(Entity entity) {
        return entity.canBeHitByProjectile();
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(
                this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
        );
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

    public void setShouldRevertNow(boolean shouldRevertNow) {
        this.dataTracker.set(SHOULD_REVERT_NOW, shouldRevertNow);
    }

    public boolean getShouldRevertNow() {
        return this.dataTracker.get(SHOULD_REVERT_NOW);
    }
}
