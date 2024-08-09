package dev.enjarai.trickster.block;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellCircleBlockEntity extends BlockEntity {
    public static final int LISTENER_RADIUS = 16;
    public static final float MAX_MANA = 450;
    public static final KeyedEndec<SpellPart> PART_ENDEC =
            SpellPart.ENDEC.keyed("spell", () -> null);
    public static final KeyedEndec<SpellExecutor> EXECUTOR_ENDEC =
            SpellExecutor.ENDEC.keyed("executor", () -> null);

    // Used with single-tick events
    public SpellPart spell;
    // Used with multitick events
    public SpellExecutor executor;

    public SpellCircleEvent event = SpellCircleEvent.NONE;
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

    public transient SpellSource spellSource;

    public SpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CIRCLE_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        spell = nbt.get(PART_ENDEC);
        executor = nbt.get(EXECUTOR_ENDEC);

        if (nbt.contains("event")) {
            event = SpellCircleEvent.REGISTRY.getEntry(Identifier.of(nbt.getString("event")))
                    .map(RegistryEntry.Reference::value).orElse(SpellCircleEvent.NONE);
        }

        manaPool.set(nbt.getFloat("mana"));

        lastPower = nbt.getInt("last_power");
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

        nbt.putString("event", event.id().toString());

        nbt.putFloat("mana", manaPool.get());

        nbt.putInt("last_power", lastPower);
    }

    public void tick() {
        manaPool.stdIncrease();

        if (event.isMultiTick() && !getWorld().isClient() && executor != null) {
            if (spellSource == null) {
                spellSource = new BlockSpellSource((ServerWorld) getWorld(), getPos(), this);
            }

            try {
                if (executor.run(spellSource).isPresent()) {
                    getWorld().setBlockState(getPos(), Blocks.AIR.getDefaultState());
                }
            } catch (BlunderException blunder) {
                lastError = blunder.createMessage()
                        .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")");
            } catch (Exception e) {
                lastError = Text.literal("Uncaught exception in spell: " + e.getMessage())
                        .append(" (").append(executor.getCurrentState().formatStackTrace()).append(")");
            }

            markDirty();
        }
        age++;
    }

    public void redstoneUpdate(int power) {
        if (event == SpellCircleEvent.REDSTONE_UPDATE && !getWorld().isClient() && lastPower != power) {
            lastPower = power;
            callEvent(List.of(new NumberFragment(power)));
            markDirty();
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

    public boolean callEvent(List<Fragment> arguments) {
        if (event.isMultiTick()) {
            throw new UnsupportedOperationException("Cannot call multi-tick event directly.");
        }

        try {
            return new DefaultSpellExecutor(spell, arguments).singleTickRun(new BlockSpellSource((ServerWorld) getWorld(), getPos(), this)).asBoolean().bool();
        } catch (Exception e) {
            return false;
        }
    }
}