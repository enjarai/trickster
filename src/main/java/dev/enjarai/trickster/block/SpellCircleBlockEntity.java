package dev.enjarai.trickster.block;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SpellCircleBlockEntity extends BlockEntity implements SpellColoredBlockEntity, Inventory, CrowMind {
    public static final KeyedEndec<SpellPart> PART_ENDEC =
            SpellPart.ENDEC.keyed("spell", () -> null);
    public static final KeyedEndec<Fragment> CROW_MIND_ENDEC =
            Fragment.ENDEC.keyed("crow_mind", () -> VoidFragment.INSTANCE);
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
    public Fragment crowMind = VoidFragment.INSTANCE;
    public int[] colors = new int[]{0xffffff};

    private Optional<ItemStack> stack = Optional.empty();

    public SpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CIRCLE_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        spell = nbt.get(PART_ENDEC);
        executor = nbt.get(EXECUTOR_ENDEC);
        lastError = nbt.get(ERROR_ENDEC);

        stack = ItemStack.fromNbt(registryLookup, nbt.get("stack"));//.flatMap(s -> s == ItemStack.EMPTY ? Optional.empty() : Optional.of(s));
        crowMind = nbt.get(CROW_MIND_ENDEC);
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

        stack.ifPresent(s -> nbt.put("stack", s.encode(registryLookup, new NbtCompound())));
        nbt.put(CROW_MIND_ENDEC, crowMind);
        nbt.putInt("last_power", lastPower);
        nbt.putIntArray("colors", colors);
    }

    public void tick() {
        if (!getWorld().isClient() && executor != null && lastError == null) {
            var spellSource = new BlockSpellSource<>((ServerWorld) getWorld(), getPos(), this);

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

    @Override
    public void clear() {
        markDirty();
        this.stack = Optional.empty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        markDirty();
        return stack.orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty() || stack.get() == ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        markDirty();
        var result = this.stack.orElse(ItemStack.EMPTY);
        this.stack = Optional.empty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        markDirty();
        return this.stack.map(s -> {
            if (s.getCount() > amount) {
                return s.split(amount);
            } else {
                this.stack = Optional.empty();
                return s;
            }
        }).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        markDirty();
        this.stack = stack == ItemStack.EMPTY ? Optional.empty() : Optional.of(stack);
    }

    @Override
    public int size() {
        return 1;
    }

	@Override
	public void setCrowMind(Fragment fragment) {
        crowMind = fragment;
	}

	@Override
	public Fragment getCrowMind() {
        return crowMind;
	}
}
