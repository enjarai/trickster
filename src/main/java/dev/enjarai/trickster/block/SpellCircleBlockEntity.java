package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpellCircleBlockEntity extends BlockEntity {
    public SpellPart spell = new SpellPart();

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
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        SpellPart.CODEC.encodeStart(NbtOps.INSTANCE, spell)
                .resultOrPartial(err -> Trickster.LOGGER.warn("Failed to save spell in spell circle: {}", err))
                .ifPresent(element -> nbt.put("spell", element));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
