package dev.enjarai.trickster.block;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import io.wispforest.endec.impl.KeyedEndec;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.SpellCoreItem;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ModularSpellConstructBlockEntity extends BlockEntity implements Inventory, CrowMind, SpellExecutionManager, SpellCastingBlockEntity {
    public static final KeyedEndec<List<Optional<SpellExecutor>>> EXECUTORS_ENDEC = SpellExecutor.ENDEC
            .optionalOf().listOf().keyed("executors", List.of());

    public final DefaultedList<Optional<SpellExecutor>> executors = DefaultedList.ofSize(4, Optional.empty());
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private Fragment crowMind = VoidFragment.INSTANCE;
    public int age;

    public ModularSpellConstructBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MODULAR_SPELL_CONSTRUCT_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);

        executors.clear();
        var newExecutors = nbt.get(EXECUTORS_ENDEC);
        for (int i = 0; i < 4; i++) {
            executors.set(i, newExecutors.get(i));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, true, registryLookup);

        nbt.put(EXECUTORS_ENDEC, executors);
    }

    public void tick() {
        age++;
        boolean updateClient = false;

        if (getWorld() instanceof ServerWorld serverWorld) {
            var source = new BlockSpellSource<>(serverWorld, getPos(), this);

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.get(i);
                var executorSlot = i - 1;

                if (stack.getItem() instanceof SpellCoreItem item && executors.get(executorSlot).isPresent()) {
                    var executor = executors.get(executorSlot).get();
                    var error = Optional.<Text>empty();

                    if (executor instanceof ErroredSpellExecutor) {
                        continue;
                    }

                    try {
                        if (executor.run(source, new TickData().withSlot(i).withBonusExecutions(item.getExecutionBonus())).isPresent()) {
                            executors.set(executorSlot, Optional.empty());
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
                    }

                    error.ifPresent(e -> {
                        executors.set(executorSlot, Optional.of(new ErroredSpellExecutor(executor.spell(), e)));
                    });
                    if (error.isPresent()) {
                        playCastSound(serverWorld, getPos(), 0.5f, 0.1f);
                        updateClient = true;
                    }
                }

                ManaComponent.tryRecharge(
                        serverWorld,
                        getPos()
                                .toCenterPos()
                                .add(
                                        new Vec3d(
                                                getCachedState()
                                                        .get(ModularSpellConstructBlock.FACING)
                                                        .getUnitVector()
                                                        .mul(0.3f, new Vector3f())
                                        )
                                ),
                        stack
                );
            }

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
    public void clear() {
        inventory.clear();
        markDirtyAndUpdateClients();
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < inventory.size()) {
            ItemStack itemStack = inventory.get(slot);
            inventory.set(slot, ItemStack.EMPTY);
            markDirtyAndUpdateClients();

            if (world instanceof ServerWorld world && itemStack.getItem() instanceof SpellCoreItem item) {
                var perhapsExecutor = executors.get(slot - 1);
                perhapsExecutor.ifPresent(executor -> itemStack.set(ModComponents.SPELL_CORE, SpellCoreComponent.of(executor)));

                if (perhapsExecutor.isEmpty()) {
                    itemStack.remove(ModComponents.SPELL_CORE);
                }

                if (item.onRemoved(world, getPos(), itemStack, perhapsExecutor)) {
                    return ItemStack.EMPTY;
                }

                executors.set(slot - 1, Optional.empty());
            }

            return itemStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (
            slot == 0
                    ? stack.isIn(ModItems.MANA_KNOTS)
                    : stack.getItem() instanceof SpellCoreItem
        ) {
            if (stack.getItem() instanceof SpellCoreItem) {
                var fragment = stack.get(ModComponents.FRAGMENT);

                if (fragment != null && fragment.value() instanceof SpellPart spell) {
                    SpellExecutor executor = new DefaultSpellExecutor(spell, List.of());

                    if (stack.get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent comp) {
                        executor = comp.tryDeserialize()
                                .filter(e -> e.spell().equals(spell))
                                .filter(e -> !(e instanceof ErroredSpellExecutor))
                                .orElse(executor);
                    }

                    executors.set(slot - 1, Optional.of(executor));
                }
            }

            inventory.set(slot, stack);
            markDirtyAndUpdateClients();
        } else if (stack.isEmpty()) {
            removeStack(slot, 1);
        }
    }

    public void markDirtyAndUpdateClients() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isEmpty() || (slot == 0 ? stack.isIn(ModItems.MANA_KNOTS) : stack.getItem() instanceof SpellCoreItem) && getStack(slot).isEmpty();
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        components.<ContainerComponent>getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.inventory);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
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

    @Override
    public int queue(SpellExecutor executor) {
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.get(i);

            if (
                stack.getItem() instanceof SpellCoreItem
                        && (executors.get(i - 1).isEmpty()
                                || executors.get(i - 1).get() instanceof ErroredSpellExecutor)
            ) {
                executors.set(i - 1, Optional.of(executor));
                return i - 1;
            }
        }

        return -1;
    }

    @Override
    public void killAll() {
        executors.clear();
    }

    @Override
    public boolean kill(int index) {
        if (executors.get(index).isPresent()) {
            executors.set(index, Optional.empty());
            return true;
        }

        return false;
    }
}
