package dev.enjarai.trickster.block;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.block.BlockState;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SpellConstructBlockEntity extends BlockEntity implements SpellColoredBlockEntity, Inventory, CrowMind, SpellCastingBlockEntity {
    public static final KeyedEndec<Fragment> CROW_MIND_ENDEC =
            Fragment.ENDEC.keyed("crow_mind", () -> VoidFragment.INSTANCE);

    public int age;
    public Fragment crowMind = VoidFragment.INSTANCE;
    public int[] colors = new int[]{0xffffff};

    private ItemStack stack = ItemStack.EMPTY;

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
        age++;
        boolean updateClient = false;

        if (getWorld() instanceof ServerWorld serverWorld) {
            var coreComponent = getComponents().get(ModComponents.SPELL_CORE);

            if (coreComponent != null) {
                var source = new BlockSpellSource<>(serverWorld, getPos(), this);
                var executor = coreComponent.executor();

                if (!(executor instanceof ErroredSpellExecutor)) {
                    var error = Optional.<Text>empty();

                    try {
                        if (executor.run(source).isPresent()) {
                            setComponents(getComponents().filtered(type -> !ModComponents.SPELL_CORE.equals(type)));
                        }
                    } catch (BlunderException blunder) {
                        error = Optional.of(blunder.createMessage()
                                .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")"));
                    } catch (Exception e) {
                        error = Optional.of(Text.literal("Uncaught exception in spell: " + e.getMessage())
                                .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")"));
                    }

                    error.ifPresent(e -> {
                        setComponents(ComponentMap.builder()
                                .addAll(getComponents()).add(ModComponents.SPELL_CORE, coreComponent.fail(e)).build());
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
        markDirtyAndUpdateClients();
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
