package dev.enjarai.trickster.block;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
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

    public void tick() {
        if (getWorld().isClient)
            return;

        var source = new BlockSpellSource<>((ServerWorld) getWorld(), getPos(), this);

        for (var stack : inventory) {
            if (stack.isOf(ModItems.SPELL_CORE) && stack.contains(ModComponents.SPELL_CORE)) {
                var slot = stack.get(ModComponents.SPELL_CORE);
                var executor = slot.executor();
                var error = Optional.<Text>empty();

                if (slot.error().isPresent())
                    continue;

                try {
                    if (executor.run(source).isPresent()) {
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
        this.inventory.clear();
        markDirty();
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        markDirty();

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 2
                ? stack.isIn(ModItems.MANA_CRYSTALS)
                : stack.isOf(ModItems.SPELL_CORE)) {
            if (stack.contains(ModComponents.SPELL)) { //TODO: when merging the macro PR, THIS MUST BE FIXED
                var spell = stack.get(ModComponents.SPELL).spell();
                
                if (!stack.contains(ModComponents.SPELL_CORE)
                        || stack.get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent comp
                        && !spell.equals(comp.spell().orElse(null))) {
                    stack.set(ModComponents.SPELL_CORE, new SpellCoreComponent(spell));
                }
            }

            this.inventory.set(slot, stack);
            markDirty();
        } else if (stack.isEmpty()) {
            this.removeStack(slot, 1);
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
        return (slot == 2 ? stack.isIn(ModItems.MANA_CRYSTALS) : stack.isOf(ModItems.SPELL_CORE)) && this.getStack(slot).isEmpty();
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
            if (stack.isOf(ModItems.SPELL_CORE) && !stack.contains(ModComponents.SPELL_CORE)) {
                stack.set(ModComponents.SPELL_CORE, new SpellCoreComponent(executor, Optional.empty(), Optional.empty()));
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
