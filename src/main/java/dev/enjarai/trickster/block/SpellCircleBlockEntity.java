package dev.enjarai.trickster.block;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
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
    public static final KeyedEndec<Fragment> CROW_MIND_ENDEC =
            Fragment.ENDEC.keyed("crow_mind", () -> VoidFragment.INSTANCE);

    public int age;
    public Fragment crowMind = VoidFragment.INSTANCE;
    public int[] colors = new int[]{0xffffff};

    private ItemStack stack = ItemStack.EMPTY;

    public SpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CIRCLE_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("stack")) {
            stack = ItemStack.fromNbt(registryLookup, nbt.get("stack")).orElseThrow();
        } else {
            stack = ItemStack.EMPTY;
        }

        crowMind = nbt.get(CROW_MIND_ENDEC);
        colors = nbt.getIntArray("colors");

        if (colors.length == 0) {
            colors = new int[]{0xffffff};
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (!stack.isEmpty()) {
            nbt.put("stack", stack.encode(registryLookup, new NbtCompound()));
        }

        nbt.put(CROW_MIND_ENDEC, crowMind);
        nbt.putIntArray("colors", colors);
    }

    public void tick() {
        var coreComponent = getComponents().get(ModComponents.SPELL_CORE);

        if (!getWorld().isClient() && coreComponent != null) {
            var source = new BlockSpellSource<>((ServerWorld) getWorld(), getPos(), this);
            var executor = coreComponent.executor();

            if (!(executor instanceof ErroredSpellExecutor)) {
                var error = Optional.<Text>empty();

                try {
                    if (executor.run(source).isPresent()) {
                        setComponents(getComponents().filtered(type -> !ModComponents.SPELL_CORE.equals(type)));
                    }
                } catch (BlunderException blunder) {
                    error = Optional.of(blunder.createMessage()
                            .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")"));
                } catch (Exception e) {
                    error = Optional.of(Text.literal("Uncaught exception in spell: " + e.getMessage())
                            .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")"));
                }

                error.ifPresent(e -> {
                    setComponents(ComponentMap.builder()
                            .addAll(getComponents()).add(ModComponents.SPELL_CORE, coreComponent.fail(e)).build());
                    markDirty();
                });
            }
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
        markDirty();
    }

    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        var result = this.stack.copyAndEmpty();
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        var result = stack.split(amount);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stack = stack;
        markDirty();
    }

    @Override
    public int size() {
        return 1;
    }

	@Override
	public void setCrowMind(Fragment fragment) {
        crowMind = fragment;
        markDirty();
    }

	@Override
	public Fragment getCrowMind() {
        return crowMind;
	}

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }
}
