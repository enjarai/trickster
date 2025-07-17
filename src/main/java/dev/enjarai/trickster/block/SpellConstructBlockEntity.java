package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.block.BlockState;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SpellConstructBlockEntity extends BlockEntity implements SpellColoredBlockEntity, Inventory, CrowMind, SpellCastingBlockEntity {
    public static final KeyedEndec<Fragment> CROW_MIND_ENDEC = Fragment.ENDEC.keyed("crow_mind", () -> VoidFragment.INSTANCE);
    public static final KeyedEndec<SpellExecutor> EXECUTOR_ENDEC = SpellExecutor.ENDEC.nullableOf().keyed("executor", () -> null);
    public static final KeyedEndec<SpellExecutor> EXECUTOR_NET_ENDEC = SpellExecutor.NET_ENDEC.nullableOf().keyed("executor", () -> null);

    public int age;
    public Fragment crowMind = VoidFragment.INSTANCE;
    public int[] colors = new int[] { 0xffffff };

    private ItemStack stack = ItemStack.EMPTY;
    public SpellExecutor executor = null;

    public SpellConstructBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CONSTRUCT_ENTITY, pos, state);
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

        executor = nbt.getBoolean("net") ? nbt.get(EXECUTOR_NET_ENDEC) : nbt.get(EXECUTOR_ENDEC);

        if (colors.length == 0) {
            colors = new int[] { 0xffffff };
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        writeCommonNbt(nbt, registryLookup);

        nbt.put(CROW_MIND_ENDEC, crowMind);
        nbt.put(EXECUTOR_ENDEC, executor);
    }

    protected void writeNetNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        writeCommonNbt(nbt, registryLookup);

        nbt.putBoolean("net", true);
        nbt.put(EXECUTOR_NET_ENDEC, executor);
    }

    protected void writeCommonNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (!stack.isEmpty()) {
            nbt.put("stack", stack.encode(registryLookup, new NbtCompound()));
        }

        nbt.putIntArray("colors", colors);
    }

    public void tick() {
        age++;
        boolean updateClient = false;

        if (getWorld() instanceof ServerWorld serverWorld) {
            if (executor != null) {
                var source = new BlockSpellSource<>(serverWorld, getPos(), this);

                if (!(executor instanceof ErroredSpellExecutor)) {
                    var error = Optional.<Text>empty();

                    boolean canUseMana = true;
                    var executionLimit = Trickster.CONFIG.maxExecutionsPerSpellPerTick();
                    if (stack.getItem() instanceof KnotItem knotItem) {
                        executionLimit = (int) (executionLimit * knotItem.getConstructExecutionLimitMultiplier(stack));
                        //noinspection DataFlowIssue
                        canUseMana = stack.get(ModComponents.MANA).pool().getMax(serverWorld) > 0;
                    }

                    try {
                        if (executor.run(source, new TickData().withCanUseMana(canUseMana).withExecutionLimit(executionLimit)).isPresent()) {
                            executor = null;
                            updateClient = true;
                        }
                    } catch (BlunderException blunder) {
                        error = Optional.of(
                                blunder.createMessage()
                                        .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")")
                        );
                    } catch (Throwable e) {
                        error = Optional.of(
                                Text.literal("Uncaught exception in spell: " + e.getMessage())
                                        .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")")
                        );
                        Trickster.LOGGER.error("Uncaught exception in spell", e);
                    }

                    error.ifPresent(e -> {
                        executor = new ErroredSpellExecutor(executor.spell(), e);
                    });

                    if (error.isPresent()) {
                        playCastSound(serverWorld, getPos(), 0.5f, 0.1f);
                        updateClient = true;
                    }
                }
            }

            ManaComponent.tryRecharge(serverWorld, getPos().toCenterPos(), stack);

            if (updateClient) {
                markDirtyAndUpdateClients();
            } else {
                markDirty();
            }
        }
    }

    public void refreshExecutor() {
        if (getComponents().contains(ModComponents.FRAGMENT)) {
            var fragment = getComponents().get(ModComponents.FRAGMENT).value();

            if (fragment instanceof SpellPart spell) {
                if (
                    executor == null
                            || !spell.equals(executor.spell())
                            || executor instanceof ErroredSpellExecutor
                ) {
                    executor = new DefaultSpellExecutor(spell, List.of());
                    markDirtyAndUpdateClients();
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = new NbtCompound();
        writeNetNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    public int[] getColors() {
        return colors;
    }

    @Override
    public void setColors(int[] colors) {
        this.colors = colors;
        markDirtyAndUpdateClients();
    }

    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
        markDirtyAndUpdateClients();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot != 0)
            return ItemStack.EMPTY;

        return stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0)
            return ItemStack.EMPTY;

        var result = stack.copyAndEmpty();
        markDirtyAndUpdateClients();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0)
            return ItemStack.EMPTY;

        var result = stack.split(amount);
        markDirtyAndUpdateClients();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0)
            return;

        this.stack = stack;
        markDirtyAndUpdateClients();
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

    public void markDirtyAndUpdateClients() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }
}
