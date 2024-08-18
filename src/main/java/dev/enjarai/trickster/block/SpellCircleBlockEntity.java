package dev.enjarai.trickster.block;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SpellCircleBlockEntity extends BlockEntity implements SpellColoredBlockEntity {
    public static final float MAX_MANA = 450;
    public static final KeyedEndec<SpellPart> PART_ENDEC =
            SpellPart.ENDEC.keyed("spell", () -> null);
    public static final KeyedEndec<SpellExecutor> EXECUTOR_ENDEC =
            SpellExecutor.ENDEC.keyed("executor", () -> null);
    public static final KeyedEndec<Text> ERROR_ENDEC =
            CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC).keyed("last_error", () -> null);

    // Used for rendering
    public SpellPart spell;
    // Used for execution
    public SpellExecutor executor;

    public Text lastError;
    public int age;
    public int lastPower;
    public CrowMind crowMind = new CrowMind(VoidFragment.INSTANCE);
    public SimpleManaPool manaPool = new SimpleManaPool(MAX_MANA) {
        @Override
        public void set(float value) {
            super.set(value);
            markDirty();
        }

        @Override
        public void stdIncrease() {
            stdIncrease(2);
        }
    };
    public int[] colors = new int[]{0xffffff};

    public transient SpellSource spellSource;

    public SpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CIRCLE_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        spell = nbt.get(PART_ENDEC);
        executor = nbt.get(EXECUTOR_ENDEC);
        lastError = nbt.get(ERROR_ENDEC);

        manaPool.set(nbt.getFloat("mana"));
        lastPower = nbt.getInt("last_power");
        colors = nbt.getIntArray("colors");

        if (colors.length == 0) {
            colors = new int[]{0xffffff};
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);


        if (spell != null) {
            nbt.put(PART_ENDEC, spell);
        }

        if (executor != null) {
            nbt.put(EXECUTOR_ENDEC, executor);
        }

        if (lastError != null) {
            nbt.put(ERROR_ENDEC, lastError);
        }

        nbt.putFloat("mana", manaPool.get());
        nbt.putInt("last_power", lastPower);
        nbt.putIntArray("colors", colors);
    }

    public void tick() {
        manaPool.stdIncrease();

        if (!getWorld().isClient() && executor != null && lastError == null) {
            if (spellSource == null) {
                spellSource = new BlockSpellSource((ServerWorld) getWorld(), getPos(), this);
            }

            try {
                if (executor.run(spellSource).isPresent()) {
                    completionEffects((ServerWorld) getWorld(), 1.2f, 0.1f, 40);
                    getWorld().setBlockState(getPos(), Blocks.AIR.getDefaultState());
                }
            } catch (BlunderException blunder) {
                lastError = blunder.createMessage()
                        .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")");
                completionEffects((ServerWorld) getWorld(), 0.5f, 0.1f, 10);
            } catch (Exception e) {
                lastError = Text.literal("Uncaught exception in spell: " + e.getMessage())
                        .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")");
                completionEffects((ServerWorld) getWorld(), 0.5f, 0.1f, 10);
            }

            markDirty();
        }
        age++;
    }

    protected void completionEffects(ServerWorld world, float startPitch, float pitchRange, int particleCount) {
        // Put the particle origin roughly in the middle of the actual model
        var centerPos = getPos().toCenterPos().subtract(
                new Vec3d(getCachedState().get(SpellCircleBlock.FACING).getUnitVector()).multiply(1.0/16*7));
        var random = world.getRandom();

        world.playSound(
                null, centerPos.x, centerPos.y, centerPos.z, ModSounds.CAST, SoundCategory.PLAYERS,
                1f, ModSounds.randomPitch(startPitch, pitchRange)
        );
        for (var color : colors) {
            world.spawnParticles(
                    new SpellParticleOptions(color), centerPos.x, centerPos.y, centerPos.z, particleCount,
                    random.nextFloat() * 0.02f - 0.01f,
                    random.nextFloat() * 0.02f + 0.01f,
                    random.nextFloat() * 0.02f - 0.01f,
                    0.1
            );
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public int[] getColors() {
        return colors;
    }

    @Override
    public void setColors(int[] colors) {
        this.colors = colors;
    }
}