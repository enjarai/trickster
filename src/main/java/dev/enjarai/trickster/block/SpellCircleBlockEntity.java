package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpellCircleBlockEntity extends BlockEntity {
//    public SpellPart spell = new SpellPart(
//            new PatternGlyph(1, 2, 3, 4),
//            List.of(
//                    Optional.of(new SpellPart(
//                            new PatternGlyph(2, 3, 6, 7),
//                            List.of()
//                    )),
//                    Optional.of(new SpellPart(
//                            new PatternGlyph(2, 3, 6, 7),
//                            List.of(
//                                    Optional.of(new SpellPart(
//                                            new PatternGlyph(8, 6, 4),
//                                            List.of()
//                                    )),
//                                    Optional.of(new SpellPart(
//                                            new SpellPart(
//                                                    new PatternGlyph(8, 6, 4),
//                                                    List.of()
//                                            ),
//                                            List.of(
//                                                    Optional.of(new SpellPart(
//                                                            new PatternGlyph(8, 6, 4),
//                                                            List.of()
//                                                    ))
//                                            )
//                                    )),
//                                    Optional.empty(),
//                                    Optional.empty()
//                            )
//                    )),
//                    Optional.of(new SpellPart(
//                            new PatternGlyph(1, 7, 5, 8),
//                            List.of(
//                                    Optional.of(new SpellPart(
//                                            new PatternGlyph(8, 6, 4),
//                                            List.of()
//                                    ))
//                            )
//                    ))
//            )
//    );
    public SpellPart spell = new SpellPart();
    public SpellCircleEvent event = SpellCircleEvent.NONE;

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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
