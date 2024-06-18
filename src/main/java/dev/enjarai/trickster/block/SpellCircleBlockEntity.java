package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.BlockSpellContext;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpellCircleBlockEntity extends BlockEntity {
    public static final int LISTENER_RADIUS = 16;

    public SpellPart spell = new SpellPart();
    public SpellCircleEvent event = SpellCircleEvent.NONE;
    public Text lastError;
    public int age;

    public SpellCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPELL_CIRCLE_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("spell")) {
            SpellPart.CODEC.decode(NbtOps.INSTANCE, nbt.get("spell"))
                    .resultOrPartial(err -> Trickster.LOGGER.warn("Failed to load spell in spell circle: {}", err))
                    .ifPresent(pair -> spell = pair.getFirst());
        }

        if (nbt.contains("event")) {
            event = SpellCircleEvent.REGISTRY.getEntry(Identifier.of(nbt.getString("event")))
                    .map(RegistryEntry.Reference::value).orElse(SpellCircleEvent.NONE);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        SpellPart.CODEC.encodeStart(NbtOps.INSTANCE, spell)
                .resultOrPartial(err -> Trickster.LOGGER.warn("Failed to save spell in spell circle: {}", err))
                .ifPresent(element -> nbt.put("spell", element));

        nbt.putString("event", event.id().toString());
    }

    public void tick() {
        if (event == SpellCircleEvent.TICK) {
            if (age % 10 == 0) {
                var iterations = age / 10;
                callEvent(List.of(new NumberFragment(iterations)));
            }

            age++;
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
        var ctx = new BlockSpellContext((ServerWorld) getWorld(), getPos());
        ctx.pushPartGlyph(arguments);
        var result = spell.runSafely(ctx, err -> lastError = err);
        ctx.popPartGlyph();
        return result.orElse(BooleanFragment.FALSE).asBoolean().bool();
    }
}
