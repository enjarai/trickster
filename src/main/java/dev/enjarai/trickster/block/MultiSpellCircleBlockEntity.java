package dev.enjarai.trickster.block;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.SpellCoreItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
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

public class MultiSpellCircleBlockEntity extends BlockEntity implements Inventory, CrowMind, SpellExecutionManager {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private Fragment crowMind = VoidFragment.INSTANCE;
    public int age;

    public MultiSpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MULTI_SPELL_CIRCLE_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, true, registryLookup);
    }

	@SuppressWarnings("resource")
    public void tick() {
        age++;

        if (getWorld().isClient)
            return;

        var source = new BlockSpellSource<>((ServerWorld) getWorld(), getPos(), this);

        for (var stack : inventory) {
            if (stack.getItem() instanceof SpellCoreItem item && stack.contains(ModComponents.SPELL_CORE)) {
                var slot = stack.get(ModComponents.SPELL_CORE);
                var executor = slot.executor();
                var error = Optional.<Text>empty();

                if (slot.executor() instanceof ErroredSpellExecutor)
                    continue;

                try {
                    if (executor.run(source, new TickData(item.getExecutionBonus())).isPresent()) {
                        stack.remove(ModComponents.SPELL_CORE);
                    }
                } catch (BlunderException blunder) {
                    error = Optional.of(blunder.createMessage()
                            .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")"));
                } catch (Exception e) {
                    error = Optional.of(Text.literal("Uncaught exception in spell: " + e.getMessage())
                            .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")"));
                }

                error.ifPresent(e -> stack.set(ModComponents.SPELL_CORE, slot.fail(e)));
            }
        }

        markDirty();
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
        markDirty();
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
            markDirty();

            if (world instanceof ServerWorld world && itemStack.getItem() instanceof SpellCoreItem item) {
                if (item.onRemoved(world, getPos(), itemStack)) {
                    return ItemStack.EMPTY;
                }
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
        if (slot == 2
                ? stack.isIn(ModItems.MANA_CRYSTALS)
                : stack.getItem() instanceof SpellCoreItem) {
            if (stack.getItem() instanceof SpellCoreItem) {
                SpellCoreComponent.refresh(stack.getComponents(),
                        component -> stack.set(ModComponents.SPELL_CORE, component));
            }

            inventory.set(slot, stack);
            markDirty();
        } else if (stack.isEmpty()) {
            removeStack(slot, 1);
        }
    }

    @Override
    public void markDirty() {
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
        return (slot == 2 ? stack.isIn(ModItems.MANA_CRYSTALS) : stack.getItem() instanceof SpellCoreItem) && getStack(slot).isEmpty();
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
	}

	@Override
	public Fragment getCrowMind() {
        return crowMind;
	}

	@Override
	public boolean queue(SpellExecutor executor) {
        for (var stack : inventory) {
            if (stack.getItem() instanceof SpellCoreItem
                    && (!stack.contains(ModComponents.SPELL_CORE)
                        || stack.get(ModComponents.SPELL_CORE).executor() instanceof ErroredSpellExecutor)) {
                stack.set(ModComponents.SPELL_CORE, new SpellCoreComponent(executor));
                return true;
            }
        }

        return false;
	}

	@Override
	public void killAll() {
        for (var stack : inventory) {
            if (stack.contains(ModComponents.SPELL_CORE))
                stack.remove(ModComponents.SPELL_CORE);
        }
	}

	@Override
	public void kill(int index) {
        var slot = index > 1 ? index + 1 : index; // accounting for the battery slot
        var stack = getStack(slot);

        if (stack.contains(ModComponents.SPELL_CORE))
            stack.remove(ModComponents.SPELL_CORE);
	}
}
